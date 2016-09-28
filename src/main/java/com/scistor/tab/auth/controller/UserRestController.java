package com.scistor.tab.auth.controller;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.scistor.tab.auth.common.Constants;
import com.scistor.tab.auth.common.HttpStatusException;
import com.scistor.tab.auth.model.Authority;
import com.scistor.tab.auth.model.Group;
import com.scistor.tab.auth.model.User;
import com.scistor.tab.auth.repository.GroupRepository;
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
 * TODO: privilege judge.
 *
 * @author Wei Xing
 */
@RequestMapping("/users")
@RestController
public class UserRestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;
    
    private static final Logger log = LoggerFactory.getLogger(UserRestController.class); 

//    @Autowired
//    public UserRestController(UserRepository userRepository, GroupRepository groupRepository) {
//        this.userRepository = userRepository;
//        this.groupRepository = groupRepository;
//    }


    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test() {
        return "test";
    }


    /**
     * Create a new user.
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestParam(required = false) String username,
                           @RequestParam(required = false) String password,
                           @RequestParam(required = false) final String email,
                           @RequestParam(required = false) final String contact,
                           @RequestParam(required = false) final Boolean isAdmin,
                           @RequestParam(required = false) final String groupId) {
        try {
            validateUserName(username);
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);
            user.setContact(contact);
            user.setGroupId(groupId);
            if (isAdmin) {
                Set<Authority> authorities = Sets.newHashSet(Authority.ADMIN);
            	user.setAuthorities(authorities);
            	user.setGroupId(Constants.DEFAULT_ADMIN_GROUP_ID);
            }
            // group id not exist, throw exception.
            Group group = groupRepository.findOne(user.getGroupId());
            if (group == null) {
                throw new HttpStatusException.BadRequest("Group is not exist.");
            }
            user.setGroup(group);
    
            if (Strings.isNullOrEmpty(user.getUsername()) || Strings.isNullOrEmpty(user.getPassword())) {
                throw new HttpStatusException.BadRequest("Username or password is not set or empty.");
            }
    
            if (userRepository.findByUsername(user.getUsername()) != null) {
                throw new HttpStatusException.Conflict("");
            }
            // Set new id, create time and last modified time .etc.
            user.setId(UUID.randomUUID().toString());
            Date now = new Date();
            user.setCreateTime(now);
            user.setLastModifiedTime(now);
            return userRepository.save(user);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } 
    }


    @RequestMapping(value = "/{ids}", method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable String[] ids) {
        try {
            List<User> deleteList = (List<User>)userRepository.findAll(Arrays.asList(ids));
            Iterator<User> listIt = deleteList.iterator();
            while(listIt.hasNext()) {
            	String id = listIt.next().getId();
                validate(id);
                userRepository.delete(id);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } 
    }
    
    /**
     * Update a user, the field can be changed: password.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public User updateUser(@PathVariable String id,
            @RequestParam(required = false) final String username,
            @RequestParam(required = false) final String password,
            @RequestParam(required = false) final String email,
            @RequestParam(required = false) final String contact,
            @RequestParam(required = false) final String groupId) {
        try {
            validate(id);
            User user = userRepository.findOne(id);
            if (username != null && !username.equals(user.getUsername())) {
                validateUserName(username);
            }
            if (username != null) {
                user.setUsername(username);
            }
            if (password != null) {
                user.setPassword(password);
            }
            if(email != null) {
                user.setEmail(email);
            }
            if(contact != null) {
                user.setContact(contact);
            }
            if(groupId != null) {
                Group group = groupRepository.findOne(groupId);
                if (group == null) {
                    log.error("Group [%s] is not exist");
                    throw new HttpStatusException.BadRequest("Group is not exist.");
                }
                user.setGroup(group);            
            }        
            return userRepository.save(user);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } 
    }

    /*
     * @api {GET} /users 查询用户
     * @author WenSenlin
     */
    @RequestMapping(method = RequestMethod.GET)
    Page<User> getPageUsers(
            @RequestParam(required = false) final String exactUserName,
            @RequestParam(required = false) final String username,
            @RequestParam(required = false) final String groupName,
            @PageableDefault(sort = {"username"}, direction = Sort.Direction.ASC) Pageable pageable) throws IOException {
        try {
            Specification<User> spec = new Specification<User>() {
                @Override
                public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                    List<Predicate> list = new ArrayList<Predicate>();
                    if (!StringUtils.isEmpty(exactUserName)) {
                        Expression<String> expression = root.get("username").as(String.class);
                        list.add(cb.equal(expression, exactUserName));
                    }
                    if (!StringUtils.isEmpty(username)) {
                        Expression<String> expression = root.get("username").as(String.class);
                        list.add(cb.like(expression, "%" + username + "%"));
                    }
                    //admin user not print
                    Expression<String> expression = root.get("username").as(String.class);
                    list.add(cb.notEqual(expression, Constants.DEFAULT_ADMIN_NAME));
                    return cb.and(list.toArray(new Predicate[list.size()]));
                }
            };
            
            Page<User> page = userRepository.findAll(spec, pageable);
            if (page.getTotalElements() == 0 && !StringUtils.isEmpty(exactUserName)) {
            	return null;
            }
            return page;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } 
    }
    
    //根据给定的ID获取用户列表
    @RequestMapping(value = "/{ids}", method = RequestMethod.GET)
    public List<User> getUser(@PathVariable String[] ids) {
        return (List<User>)userRepository.findAll(Arrays.asList(ids));
    }
    
    /**
     * @throws IOException
     * @api {get} /users/all 查询所有的用户
     * @apiName WenSenlin
     */
    @RequestMapping(method = RequestMethod.GET, value = "/all")
    List<User> getAllUsers() throws IOException {
        try {
            return (List<User>)userRepository.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } 
    }

    private void validate(String id) {
        User user =
                (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(id.equals(user.getId()) || user.getAuthorities().contains(Authority.ADMIN))) {
            throw new HttpStatusException.Forbidden("");
        }
        // If the id is not exist, throw a exception.
        if (!userRepository.exists(id)) {
            throw new HttpStatusException.NotFound(id);
        }        
    }
    
    public void validateUserName(String userName) {
        if (userRepository.findByUsername(userName) != null) {
            throw new HttpStatusException.Conflict("User name \"" + userName + "\" is exist.");
        }
    }
}
