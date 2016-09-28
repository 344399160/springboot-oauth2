package com.scistor.tab.auth.util;

import com.scistor.tab.auth.common.Constants;
import com.scistor.tab.auth.common.HttpStatusException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.LinkedHashMap;
import java.util.List;


public class ValidUtils{
    /**
     *获取当前登录用户 ID
     **/
    public static String getLoginUserId(){
        return ((LinkedHashMap<?, ?>) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).get("id").toString();
    }
    
    /**
     *获取当前登录用户所属组 ID
     **/
    public static String getLoginGroupId(){
        return ((LinkedHashMap<?, ?>) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).get("groupId").toString();
    }

    public static String getLoginUserName(){
        return ((LinkedHashMap<?, ?>) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).get("username").toString();
    }
    
    public static String getLoginGroupName(){
        return ((LinkedHashMap<?, ?>) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).get("groupName").toString();
    }
    
    public static List<String> getLoginUserAuthority(){
        return (List<String>)((LinkedHashMap<?, ?>) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).get("authorities");
    }

    public static String getLoginQueueName(){
    	Object queueName = ((LinkedHashMap<?, ?>) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).get("resourcePoolQueueName");
        if (queueName == null) {
        	return Constants.DEFAULT_QUEUE_NAME;
        } else {
        	return queueName.toString();
        }
    }
    
    public static void validate(String id) throws Exception {
        if (!id.equals(getLoginUserId()) && !ValidUtils.getLoginUserAuthority().contains("ADMIN")) {
            throw new HttpStatusException.Forbidden("No permission!");
        }
    }
 }
