package com.scistor.tab.auth.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scistor.tab.auth.common.Constants;
import com.scistor.tab.auth.common.HttpStatusException;
import com.scistor.tab.auth.model.Authority;
import com.scistor.tab.auth.model.Group;
import com.scistor.tab.auth.model.ResourcePool;
import com.scistor.tab.auth.model.User;
import com.scistor.tab.auth.repository.ComputeResourceRepository;
import com.scistor.tab.auth.repository.GroupRepository;
import com.scistor.tab.auth.repository.ResourcePoolRepository;
import com.scistor.tab.auth.util.Dom4JUtils;
import com.scistor.tab.auth.util.YarnUtil;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

/**
 * @author Wen Senlin
 */
@RestController
@RequestMapping("/resourcePools")
public class ResourcePoolController {
    
    @Value("${com.scistor.datavision.cm.url}")
    private String cmUrl;
    
    @Value("${com.scistor.datavision.cm.user}")
    private String user;
    
    @Value("${com.scistor.datavision.cm.psw}")
    private String password;
    
    @Autowired
    ResourcePoolRepository repository;
    
    @Autowired
    ComputeResourceRepository computeResourceRepository;
    
    @Autowired
    GroupRepository groupRepository;
    
    private static final Logger log = LoggerFactory.getLogger(ResourcePoolController.class); 
    
    /*
     * @api {post} /ResourcePools 新建 资源池
     * @author WenSenlin
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ResourcePool createResourcePool (
            HttpServletRequest req, HttpServletResponse resp,
            @RequestParam(required = true) final String name,
            @RequestParam(required = true) final Integer cpuNum,
            @RequestParam(required = true) final String groupId,
            @RequestParam(required = true) final Long memSize,
            @RequestParam(required = true) final Long totalMemSize) throws Exception {
        try {
            validateName(name);
            String queueName = name;
            try {
	            String groupConfigUrl = cmUrl + "/api/v9/clusters/" + YarnUtil.getClusterName(cmUrl, user, password) + "/services/" + YarnUtil.getYarnServiceName(cmUrl, user, password) +  "/roleConfigGroups/" + YarnUtil.getRMConfigGroupName(cmUrl, user, password) + "/config";
	            String config = YarnUtil.getMethod(groupConfigUrl, user, password);
	            String queueConfig = getQueueConfig(config);
	            if (queueConfig.isEmpty() || "failed".equals(queueConfig)) {
	                groupConfigUrl = cmUrl + "/api/v9/clusters/" + YarnUtil.getClusterName(cmUrl, user, password)  + "/services/" + YarnUtil.getYarnServiceName(cmUrl, user, password) +  "/roles/" + YarnUtil.getRMConfigName(cmUrl, user, password) + "/process/configFiles/capacity-scheduler.xml"; 
	                queueConfig = YarnUtil.getMethod(groupConfigUrl, user, password);
	            }
	            double newQueueCapacity = Math.round(100 * ((double)memSize / (double)totalMemSize));
	            queueConfig = updateQueueConf_createQueue(queueConfig, queueName, newQueueCapacity, cpuNum); //修改队列配置
	            if ("failed".equals(queueConfig)) { //如果请求资源不符合要求，返回错误信息
	                throw new Exception("Create queue failed!");
	            }
	            String updateConfig = updateConfig(config, queueConfig);
	            YarnUtil.putMethod(groupConfigUrl, updateConfig, user, password); //向cms发送put请求修改配置
	            String response = YarnUtil.refreshQueue_CMS(cmUrl, user, password); //刷新队列
	            if ("UNKNOWN".equals(response)) {
	                throw new Exception("refresh queue failed!");
	            }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                queueName = Constants.DEFAULT_QUEUE_NAME;
            }
            ResourcePool resourcePool = new ResourcePool();
            Group group = groupRepository.findOne(groupId);
            resourcePool.setGroup(group);
            resourcePool.setId(UUID.randomUUID().toString());
            resourcePool.setName(name);
            resourcePool.setQueueName(queueName);
            resourcePool.setCpuNum(cpuNum);
            resourcePool.setMemSize(memSize);
            resourcePool.setLastUpateTime(new Date());
            resourcePool = repository.save(resourcePool);
            group.setResourcePool(resourcePool);
            groupRepository.save(group);
            return resourcePool;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } 
    }
    
    /*
     * @api {delete} /{id} 删除资源池
     * @param ids 资源池id
     * @author WenSenlin
     */
    @RequestMapping(value = "/{ids}", method = RequestMethod.DELETE)
    public void delete(@PathVariable String[] ids) throws Exception {
        try {
            List<ResourcePool> deleteList = (List<ResourcePool>)repository.findAll(Arrays.asList(ids));
            Iterator<ResourcePool> listIt = deleteList.iterator();
            while(listIt.hasNext()) {
                ResourcePool resourcePool = listIt.next();
                if (resourcePool.getQueueName().equals(Constants.DEFAULT_QUEUE_NAME)) {
	                repository.delete(resourcePool);
	                continue;
                }
                try {
	                String groupConfigUrl = cmUrl + "/api/v9/clusters/" + YarnUtil.getClusterName(cmUrl, user, password)  + "/services/" + YarnUtil.getYarnServiceName(cmUrl, user, password) +  "/roleConfigGroups/" + YarnUtil.getRMConfigGroupName(cmUrl, user, password) + "/config"; 
	                String config = YarnUtil.getMethod(groupConfigUrl, user, password);
	                String queueConfig = getQueueConfig(config);
	                queueConfig = updateQueueConf_deleteQueue(queueConfig, resourcePool.getQueueName()); //删除队列配置
	                if ("failed".equals(queueConfig)) { //如果请求资源不符合要求，返回错误信息
	                    throw new Exception("delete failed");
	                }
	                String updateConfig = updateConfig(config, queueConfig);
	                YarnUtil.putMethod(groupConfigUrl, updateConfig, user, password); //向cms发送put请求修改配置
	                String response = YarnUtil.refreshQueue_CMS(cmUrl, user, password); //刷新队列
	                if ("UNKNOWN".equals(response)) {
	                    throw new Exception("refreshQueue failed");
	                }
	                repository.delete(resourcePool);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 修改资源池
     * @author Wen Senlin
     * @throws Exception 
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResourcePool updateResourcePool(@PathVariable String id,
            @RequestParam(required = false) final String name,
            @RequestParam(required = false) final Integer cpuNum,
            @RequestParam(required = false) final Long memSize,
            @RequestParam(required = false) final Long totalMemSize) throws Exception {
        try {    
            validate();
            ResourcePool resourcePool = repository.findOne(id);
            if (name != null && !name.equals(resourcePool.getName())) {
                validateName(name);
                resourcePool.setName(name);
            }
            if (cpuNum != null) {
                resourcePool.setCpuNum(cpuNum);
            }
            if (memSize != null) {
                resourcePool.setMemSize(memSize);
            }
            if ((cpuNum != null || (memSize != null && totalMemSize != null)) && !resourcePool.getQueueName().equals(Constants.DEFAULT_QUEUE_NAME)) {
                try {
	            	String groupConfigUrl = cmUrl + "/api/v9/clusters/" + YarnUtil.getClusterName(cmUrl, user, password)  + "/services/" + YarnUtil.getYarnServiceName(cmUrl, user, password) +  "/roleConfigGroups/" + YarnUtil.getRMConfigGroupName(cmUrl, user, password) + "/config"; 
	                String config = YarnUtil.getMethod(groupConfigUrl, user, password);
	                String queueConfig = getQueueConfig(config);
	                double newQueueCapacity = Math.round(100 * ((double)memSize / (double)totalMemSize));
	                List<Object> list = updateQueueConf_upateQueue(queueConfig, resourcePool.getQueueName(), newQueueCapacity, resourcePool.getCpuNum()); //修改队列配置
	                queueConfig = (String) list.get(0);
	                if ("failed".equals(queueConfig)) { //如果请求资源不符合要求，返回错误信息
	                    throw new Exception("updateQueue failed");
	                }
	                String updateConfig = updateConfig(config, queueConfig);
	                YarnUtil.putMethod(groupConfigUrl, updateConfig, user, password); //向cms发送put请求修改配置
	                String response = YarnUtil.refreshQueue_CMS(cmUrl, user, password); //刷新队列
	                if ("UNKNOWN".equals(response)) {
	                    throw new Exception("refreshQueue failed");
	                }
                    resourcePool.setQueueName(name);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            return repository.save(resourcePool);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }
    
    /*
     * @api {GET} /resourcePools 查询资源池列表
     * @author WenSenlin
     */
    @RequestMapping(method = RequestMethod.GET)
    Page<ResourcePool> getPageResourcePools(
            @RequestParam(required = false) final String name,
            @PageableDefault(sort = {"name"}, direction = Sort.Direction.ASC) Pageable pageable) throws IOException {
        try {
            Specification<ResourcePool> spec = new Specification<ResourcePool>() {
                @Override
                public Predicate toPredicate(Root<ResourcePool> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    List<Predicate> list = new ArrayList<Predicate>();
                    
                    if (!StringUtils.isEmpty(name)) {
                        Expression<String> ResourcePoolStr = root.get("name").as(String.class);
                        list.add(cb.like(ResourcePoolStr, "%" + name + "%"));
                    }
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };
            
            Page<ResourcePool> page = repository.findAll(spec, pageable);
            return page;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResourcePool getResourcePool(@PathVariable String id) {
        try {
            ResourcePool resourcePool = repository.findOne(id);
            if (resourcePool == null) {
                throw new HttpStatusException.NotFound("resourcePool id '" + id + "'is not exist.");
            }
            return resourcePool;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }
    
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public List<ResourcePool> getAllResourcePool() {
        return  repository.findAll();
    }
    
    private String getQueueConfig(String config) throws Exception {
        Iterator<JsonNode> qIterator = new ObjectMapper().readTree(config).path("items").elements();
        while(qIterator.hasNext()) {
            JsonNode jsonNode = qIterator.next();
            if (jsonNode.path("name").asText().equals("resourcemanager_capacity_scheduler_configuration")) {
                return jsonNode.path("value").asText();
            }
        }
        return "";
    }
    
    private String updateQueueConf_createQueue(String queueConf, String queueName,
            double newQueueCapa, double userLimitFactor) throws Exception {
        Document document = DocumentHelper.parseText(queueConf);
        Element root = document.getRootElement(); 
        Iterator<?> iterator = root.elementIterator();
        while (iterator.hasNext()) {
            Element node = (Element) iterator.next();
            if ("yarn.scheduler.capacity.root.default.capacity".equals(node.elementText("name"))) {
                double newDefaultCapa = Double.parseDouble(node.elementText("value")) - newQueueCapa;
                if (newDefaultCapa < 0) {
                    return "failed";
                }
                node.element("value").setText(String.valueOf(newDefaultCapa));
            }
            if ("yarn.scheduler.capacity.root.queues".equals(node.elementText("name"))) {
                String newQueues = node.elementText("value") + "," + queueName;
                node.element("value").setText(newQueues);
            }
        }
        Element queueCapa = root.addElement("property");
        Element queueCapaName = queueCapa.addElement("name");
        queueCapaName.setText("yarn.scheduler.capacity.root." + queueName + ".capacity");
        Element queueCapaValue = queueCapa.addElement("value");
        queueCapaValue.setText(String.valueOf(newQueueCapa));    
        
        return Dom4JUtils.formatXML(document);
    }
    
    private String updateQueueConf_deleteQueue(String queueConf, String queueName) throws Exception{
        Document document = DocumentHelper.parseText(queueConf);
        Element root = document.getRootElement();
        Iterator<?> iterator = root.elementIterator();
        String rootQueue = "yarn.scheduler.capacity.root.queues";
        String defaultQueueCapa = "yarn.scheduler.capacity.root.default.capacity";
        String updateQueueCapa = "yarn.scheduler.capacity.root." + queueName + ".capacity";
        double updatequeueCapaValue = 0;
        double defaultQueueCapaValue = 0;
        while (iterator.hasNext()) {
            Element node = (Element) iterator.next();
            if (rootQueue.equals(node.elementText("name"))) {
                String[] arr = node.elementText("value").split(",");
                List<String> queues = new ArrayList<String>(Arrays.asList(arr));
                if (queues.contains(queueName)) {
                    queues.remove(queueName);
                }
                String newQueues = StringUtils.join(queues.toArray(),",");
                node.element("value").setText(newQueues);
            }
            if (defaultQueueCapa.equals(node.elementText("name"))) {
                defaultQueueCapaValue = Double.parseDouble(node.elementText("value"));
                continue;
            }
            if (updateQueueCapa.equals(node.elementText("name"))) {
                updatequeueCapaValue = Double.parseDouble(node.elementText("value"));
                root.remove(node);
                continue;
            }
        }
        
        Iterator<?> iterator_1 = root.elementIterator();
        double newDefaultQueueCapaValue = defaultQueueCapaValue + updatequeueCapaValue;
        while (iterator_1.hasNext()) {
            Element node = (Element) iterator_1.next();
            if (defaultQueueCapa.equals(node.elementText("name"))) {
                node.element("value").setText(String.valueOf(newDefaultQueueCapaValue));
                break;
            }
        }
        return Dom4JUtils.formatXML(document);
    }
    
    /**
     * 更改队列配置，用于修改队列接口
     * @param queueConf 原队列配置
     * @param queue 需要修改的队列名称
     * @param newQueueCapa 新的队列capcity
     * @return 返回修改后队列配置信息
     * @throws Exception
     */
    private List<Object> updateQueueConf_upateQueue(String queueConf, String queueName,
            double newQueueCapa, double userLimitFactor) throws Exception {
        List<Object> list = new ArrayList<Object>();
        String strXML = "";
        Document document = DocumentHelper.parseText(queueConf); // 将字符串转化为xml
        Element node1 = document.getRootElement(); // 获得根节点
        Iterator<?> iter1 = node1.elementIterator();// 获取根节点下的子节点
        double defaultQueueCapaChange = 0;
        while (iter1.hasNext()) {
            Element node2 = (Element) iter1.next();
            String updateQueueCapa = "yarn.scheduler.capacity.root." + queueName + ".capacity";
            if (updateQueueCapa.equals(node2.elementText("name"))) {
                double oldQueueCapa = Double.parseDouble(node2.elementText("value"));
                defaultQueueCapaChange = newQueueCapa - oldQueueCapa;
                node2.element("value").setText(String.valueOf(newQueueCapa));
            }
        }
        Iterator<?> iter2 = node1.elementIterator();
        while (iter2.hasNext()) {
            Element node2 = (Element) iter2.next();
            if ("yarn.scheduler.capacity.root.default.capacity".equals(node2.elementText("name"))) {
                double newDefaultCapa = Double.parseDouble(node2.elementText("value")) - defaultQueueCapaChange;
                if (newDefaultCapa < 0) {
                    list.add("failed");
                    return list;
                }
                node2.element("value").setText(String.valueOf(newDefaultCapa));
            }
            StringWriter strWtr = new StringWriter();
            XMLWriter xmlWriter = new XMLWriter(strWtr);
            xmlWriter.write(document);
            strXML = strWtr.toString();
        }
        
        list.add(strXML);
        if (defaultQueueCapaChange != 0) {
            list.add("capacity");
            list.add(newQueueCapa);
        }
        
        return list;
    }
    
    /**
     * 修改角色配置组中的队列配置
     * @param config 角色配置组信息
     * @param queueConf 队列信息
     * @return 返回修改后的角色组配置信息
     * @throws Exception
     */
    private String updateConfig(String config, String queueConfig) throws Exception {
        JSONObject configJsonobject = new JSONObject(config);
        JSONArray jsonarray = configJsonobject.getJSONArray("items");
        for(int i = 0; i < jsonarray.length(); i++) {
            JSONObject queueJsonobject = jsonarray.getJSONObject(i);
            if ("resourcemanager_capacity_scheduler_configuration".equals(queueJsonobject.getString("name"))) {
                queueJsonobject.put("value", queueConfig);
                return configJsonobject.toString();
            }
        }
        JSONObject object = new JSONObject();
        object.put("name", "resourcemanager_capacity_scheduler_configuration");
        object.put("value", queueConfig);
        jsonarray.put(object);
        return configJsonobject.toString();
    }
    
    private void validate() {
        User user =
                (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(user.getAuthorities().contains(Authority.ADMIN))) {
            throw new HttpStatusException.Forbidden("");
        }        
    }
    
    public void validateName(String name) {
        if (repository.findByName(name) != null) {
            throw new HttpStatusException.Conflict("name \"" + name + "\" is exist.");
        }
    }
}
