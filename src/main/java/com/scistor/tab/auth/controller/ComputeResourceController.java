package com.scistor.tab.auth.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scistor.tab.auth.model.ComputeResource;
import com.scistor.tab.auth.model.ResourcePool;
import com.scistor.tab.auth.repository.ComputeResourceRepository;
import com.scistor.tab.auth.repository.ResourcePoolRepository;
import com.scistor.tab.auth.util.YarnUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author Wen Senlin
 */
@RestController
@RequestMapping("/computeResources")
public class ComputeResourceController {
    
    private static final Logger log = LoggerFactory.getLogger(ComputeResourceController.class);

    @Autowired
    ComputeResourceRepository repository;
    
    @Autowired
    ResourcePoolRepository resourcePoolRepository;
    
    /**
     * Get computeResource
     * @throws Exception 
     */
    @RequestMapping(method = RequestMethod.GET)
    public ComputeResource getComputeResources() throws Exception {
        try {
            ComputeResource computeResource  = repository.findById("cluster");
            if (computeResource == null) {
                return repository.save(getComputeResource());            
            } else {
                //TODO 超过 5分钟才进行更新 
                if (new Date().getTime() - computeResource.getLastUpateTime().getTime()  >= 300) {
                    repository.save(getComputeResource());            
                }
            }
            return checkComputeResource();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;   
        }
    }
    
    /**
     * get lastest info
     * @throws Exception 
     */
    ComputeResource getComputeResource() throws Exception {
        String rmWebappAddr = YarnUtil.getRmWebappAddr();
        String clusterInfo = YarnUtil.getMethod("http://" + rmWebappAddr + "/ws/v1/cluster/metrics", "" , "");
        JsonNode cluster = new ObjectMapper().readTree(clusterInfo).path("clusterMetrics");
        ComputeResource computeResource = new ComputeResource();
        computeResource.setId("cluster");
        computeResource.setTotalCpuNum(cluster.path("totalVirtualCores").asInt());
        computeResource.setTotalMemSize(cluster.path("totalMB").asLong());
        computeResource.setLastUpateTime(new Date());
        return computeResource;       
    }
    
    public ComputeResource checkComputeResource() {
        List<ResourcePool> rList  = resourcePoolRepository.findAll();
        Iterator<ResourcePool> it = rList.iterator();
        Integer useCpuNum = 0;
        Long useMemSize = 0L;
        while(it.hasNext()) {
            ResourcePool resourcePool = it.next();
            useCpuNum += resourcePool.getCpuNum();
            useMemSize += resourcePool.getMemSize();
        }
        ComputeResource computeResource = repository.findAll().get(0);
        computeResource.setFreeCpuNum(computeResource.getTotalCpuNum() - useCpuNum);
        computeResource.setFreeMemSize(computeResource.getTotalMemSize() - useMemSize);
        computeResource.setUseCpuNum(useCpuNum);
        computeResource.setUseMemSize(useMemSize);
        return repository.save(computeResource);
    }
}
