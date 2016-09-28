package com.scistor.tab.auth.controller;

import com.scistor.tab.auth.common.Constants;
import com.scistor.tab.auth.common.HttpStatusException;
import com.scistor.tab.auth.model.Authority;
import com.scistor.tab.auth.model.Group;
import com.scistor.tab.auth.model.ResourcePool;
import com.scistor.tab.auth.model.User;
import com.scistor.tab.auth.repository.GroupRepository;
import com.scistor.tab.auth.repository.ResourcePoolRepository;
import com.scistor.tab.auth.repository.UserRepository;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.*;
import java.io.IOException;
import java.util.*;

/**
 * Group manager.
 *
 * @author Wei Xing
 */
@RestController
@RequestMapping("/groups")
public class GroupRestController {
    
    private static final Logger log = LoggerFactory.getLogger(GroupRestController.class);

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ResourcePoolRepository resourcePoolRepository;


    @RequestMapping(method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Group createGroup(@RequestParam(required = true) final String name,
                             @RequestParam(required = false) final String description) {
        try {
            validateGroupName(name);
            Group group = new Group();
            group.setName(name);
            group.setDescription(description);
            group.setCreateTime(new Date());
            group.setUserName(getLoginUser().getUsername());
            group.setId(UUID.randomUUID().toString());
            return groupRepository.save(group);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }


    @RequestMapping(value = "/{ids}", method = RequestMethod.DELETE)
    public void deleteGroup(@PathVariable String[] ids) throws Exception {
        try {    
            validate();
            List<ResourcePool> resourcePoolList = new ArrayList<ResourcePool>();
            for (int i = 0; i < ids.length; i++) {
                Group group = groupRepository.findOne(ids[i]);
                if (null != group) {
                    List<User> userList = userRepository.findByGroup(group);
                    if(userList != null && userList.size() > 0){
                        throw new HttpStatusException.Forbidden("group exist user, delete user first!");
                    }
                } else {
                    throw new HttpStatusException.NotFound("not found group id");
                }
                resourcePoolList.add(group.getResourcePool());
            }
            List<Group> deleteList = (List<Group>)groupRepository.findAll(Arrays.asList(ids));
            groupRepository.deleteInBatch(deleteList);
            resourcePoolRepository.deleteInBatch(resourcePoolList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Group getGroup(@PathVariable String id) {
        try {
            Group group = groupRepository.findOne(id);
            if (group == null) {
                throw new HttpStatusException.NotFound("Group id '" + id + "'is not exist.");
            }
            return group;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * @throws IOException
     * @api {get} /groups/all 查询所有的用户组
     * @apiName WenSenlin
     */
    @RequestMapping(method = RequestMethod.GET, value = "/all")
    Map<String, List<Group>> getAllGroups() throws Exception {
        try {
            Map<String, List<Group>> resultMap = new HashMap<String, List<Group>>();
            resultMap.put("content", groupRepository.findAll());
            return resultMap;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Group updateGroup(@PathVariable String id, @RequestParam(required = false) final String name,
                             @RequestParam(required = false) final String description) {
         try {
            validate();
            Group group = groupRepository.getOne(id);
            if (name != null && !name.equals(group.getName())) {
                validateGroupName(name);
                group.setName(name);
            }
            if (description != null) {
                group.setDescription(description);
            }
            return groupRepository.save(group);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    Page<Group> getPageGroups(
            @RequestParam(required = false) final String name,
            @PageableDefault(sort = {"name"}, direction = Sort.Direction.ASC) Pageable pageable) throws IOException {
        try {
            Specification<Group> spec = new Specification<Group>() {
                @Override
                public Predicate toPredicate(Root<Group> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    List<Predicate> list = new ArrayList<Predicate>();
                    
                    if (!StringUtils.isEmpty(name)) {
                        Expression<String> expression = root.get("name").as(String.class);
                        list.add(cb.like(expression, "%" + name + "%"));
                    }
                    //admin group not print
                    Expression<String> expression = root.get("name").as(String.class);
                    list.add(cb.notEqual(expression, Constants.DEFAULT_ADMIN_NAME));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };
            
            Page<Group> page = groupRepository.findAll(spec, pageable);
            return page;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }
    
    public void validateGroupName(String groupName) {
        if (groupRepository.findByName(groupName) != null) {
            throw new HttpStatusException.Conflict("Group name \"" + groupName + "\" is exist.");
        }
    }
    
    private void validate() {
        User user =
                (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(user.getAuthorities().contains(Authority.ADMIN))) {
            throw new HttpStatusException.Forbidden("");
        }        
    }
    
    private User getLoginUser() {
        User user =  (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user;
    }
}
