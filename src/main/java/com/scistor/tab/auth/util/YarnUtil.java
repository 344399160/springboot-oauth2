package com.scistor.tab.auth.util;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.*;
import org.apache.hadoop.yarn.conf.HAUtil;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.util.RMHAUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import java.net.URLEncoder;

/**
 * yarn工具类
 * @author Wen Senlin
 *
 */
public class YarnUtil {
	 /**
	  * 获得ResourceManager的 active webAppAddr
	  * @return
	  */
	 public static String getRmWebappAddr() {
		 YarnConfiguration conf = new YarnConfiguration();
		 boolean isHA = HAUtil.isHAEnabled(conf);
		 String rmAddr = "";
		 if (isHA) {
			 String rmid = RMHAUtils.findActiveRMHAId(conf);
			 rmAddr = conf.get("yarn.resourcemanager.webapp.address." + rmid);
		 } else {
			 rmAddr = conf.get("yarn.resourcemanager.webapp.address");
		 }
		 return rmAddr;
	 }
	 
	  public static String getClusterName(String cmUrl, String user, String password) throws Exception {
	        String cluster = getMethod(cmUrl + "/api/v9/clusters/", user, password);
	        JSONObject jsonObject = new JSONObject(cluster);
	        String clusterName = jsonObject.getJSONArray("items").getJSONObject(0).getString("name");
	        return URLEncoder.encode(clusterName, "UTF-8");
	  }
	  
	  public static String getYarnServiceName(String cmUrl, String user, String password) throws Exception {
	        String cluster = getMethod(cmUrl + "/api/v9/clusters/" + getClusterName(cmUrl, user, password) + "/services", user, password);
	        JSONObject jsonObject = new JSONObject(cluster);
	        JSONArray items_ja = jsonObject.getJSONArray("items");
	        String yarnServiceName = "";
	        for (int i = 0; i < items_ja.length(); i++) {
	            JSONObject item_jo = items_ja.getJSONObject(i);
	            if (!"YARN".equals(item_jo.getString("type"))) {
	                continue;
	            }
	            yarnServiceName = item_jo.getString("name");
	            break;
	        }
	        return URLEncoder.encode(yarnServiceName, "UTF-8");
	  }
	  
	  public static String getRMConfigGroupName(String cmUrl, String user, String password) throws Exception {
	        String roleConfigGroups = getMethod(cmUrl + "/api/v9/clusters/" + getClusterName(cmUrl, user, password) + "/services/" + getYarnServiceName(cmUrl, user,password) + "/roleConfigGroups", user, password);
	        JSONObject jsonObject = new JSONObject(roleConfigGroups);
	        JSONArray items_ja = jsonObject.getJSONArray("items");
	        String RMConfigGroupName = "";
	        for (int i = 0; i < items_ja.length(); i++) {
	            JSONObject item_jo = items_ja.getJSONObject(i);
	            if (!"RESOURCEMANAGER".equals(item_jo.getString("roleType"))) {
	                continue;
	            }
	            RMConfigGroupName = item_jo.getString("name");
	            break;
	        }
	        return URLEncoder.encode(RMConfigGroupName, "utf8");
	  }
	  
	  public static String getRMConfigName(String cmUrl, String user, String password) throws Exception {
	        String roleConfigGroups = getMethod(cmUrl + "/api/v9/clusters/" + getClusterName(cmUrl, user, password) + "/services/" + getYarnServiceName(cmUrl, user,password) + "/roles", user, password);
	        JSONObject jsonObject = new JSONObject(roleConfigGroups);
	        JSONArray items_ja = jsonObject.getJSONArray("items");
	        String RMConfigGroupName = "";
	        for (int i = 0; i < items_ja.length(); i++) {
	            JSONObject item_jo = items_ja.getJSONObject(i);
	            if (!"RESOURCEMANAGER".equals(item_jo.getString("type"))) {
	                continue;
	            }
	            RMConfigGroupName = item_jo.getString("name");
	            break;
	        }
	        return URLEncoder.encode(RMConfigGroupName, "utf8");
	  }
	 
	  public static String getMethod(String url, String user, String password) throws Exception {
	        GetMethod getmethod = new GetMethod(url);
	        HttpClient httpclient = new HttpClient();
	           httpclient.getState().setCredentials( // 服务器认证
	                    AuthScope.ANY, // 认证域
	                    new UsernamePasswordCredentials(user, password) // 用户名、密码认证
	                    );
	        httpclient.executeMethod(getmethod);        
	        return getmethod.getResponseBodyAsString();
	    }
	  
	    /**
	     * 向cms发送put请求
	     * @param url 请求地址
	     * @param entity 请求体内容，
	     * @throws Exception
	     */
	    public static void putMethod(String url, String entity, String user, String password) throws Exception {
	        PutMethod putmethod = new PutMethod(url);
	        RequestEntity requestEntity = new ByteArrayRequestEntity(entity.getBytes());
	        putmethod.setRequestEntity(requestEntity);
	        //执行请求
	        HttpClient httpclient = new HttpClient();
	        httpclient.getState().setCredentials( // 服务器认证
	                AuthScope.ANY, // 认证域
	                new UsernamePasswordCredentials(user, password) // 用户名、密码认证
	                );
	        httpclient.executeMethod(putmethod);
	    }
	    
	    /**
	     * 向cms发送post请求,有请求实体
	     * @param url 请求地址
	     * @param entity 字符串类型请求实体
	     * @throws Exception
	     */
	    public static String postMethod(String url, String entity, String user, String password) throws Exception {
	        PostMethod postmethod = new PostMethod(url);
	        HttpClient httpclient = new HttpClient();
	        httpclient.getState().setCredentials( // 服务器认证
	                AuthScope.ANY, // 认证域
	                new UsernamePasswordCredentials(user, password) // 用户名、密码认证
	                );
	        RequestEntity requestEntity = new ByteArrayRequestEntity(entity.getBytes());
	        postmethod.setRequestEntity(requestEntity);
	        httpclient.executeMethod(postmethod);
	        String response = postmethod.getResponseBodyAsString();
	        response = new String(response.getBytes("ISO8859-1"), "UTF-8");
	        return response;
	    }
	    
	    public static String getRMRoleName(String cmUrl, String user, String password) throws Exception {
	        String roles = getMethod(cmUrl + "/api/v9/clusters/" + getClusterName(cmUrl, user, password) + "/services/" + getYarnServiceName(cmUrl, user, password) + "/roles", user, password);
	        JSONObject jsonObject = new JSONObject(roles);
	        JSONArray items_ja = jsonObject.getJSONArray("items");
	        String RMRoleName = "";
	        for (int i = 0; i < items_ja.length(); i++) {
	            JSONObject item_jo = items_ja.getJSONObject(i);
	            if ("RESOURCEMANAGER".equals(item_jo.getString("type")) && "UNKNOWN".equals(item_jo.getString("haStatus"))) {
	                return "UNKNOWN";
	            }
	            if (!"RESOURCEMANAGER".equals(item_jo.getString("type")) || !"ACTIVE".equals(item_jo.getString("haStatus"))) {
	                continue;
	            }
	            RMRoleName = item_jo.getString("name");
	            break;
	        }
	        return RMRoleName;
	    }
	    
	    /**
	     * 刷新clouderManegerService节点的队列配置，在cm界面修改队列配置保存之后，调用该接口新配置即可生效
	     * @return
	     * @throws Exception
	     */	    
	    public static String refreshQueue_CMS(String cmUrl, String user, String password) throws Exception {
	        //String url = "http://192.168.8.206:7180/api/v9/clusters/cluster/services/yarn/roleCommands/refresh"
	        String url = cmUrl + "/api/v9/clusters/" + getClusterName(cmUrl, user, password) + "/services/" + getYarnServiceName(cmUrl, user, password) + "/roleCommands/refresh";   
	        String RMRoleName = getRMRoleName(cmUrl, user, password);
	        if ("UNKNOWN".equals(RMRoleName)) {
	            return RMRoleName;
	        }
	        String jsonParams = "{'items' : ['" + getRMRoleName(cmUrl, user, password) + "']}";
	        JSONObject json = new JSONObject(jsonParams);
	        String response = postMethod(url, json.toString(), user, password);
	        return response;
	    }
}
