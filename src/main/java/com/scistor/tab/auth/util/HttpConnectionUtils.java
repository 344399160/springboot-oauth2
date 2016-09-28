package com.scistor.tab.auth.util;

import java.net.HttpURLConnection;
import java.net.URL;

public class HttpConnectionUtils {
    public static String httpPost(String urlStr) throws Exception {      
        try {
            URL url = new URL(urlStr);
//            System.out.print("+++++++++++url" + url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            String respone = "";
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                respone = IOUtils.convertStreamToString(conn.getInputStream());
            } else {
                throw new Exception(conn.getResponseMessage());
            }
            conn.disconnect();
//            System.out.print("+++++++++++respone" + respone);
            return respone;
        } catch (Exception e) {
            e.printStackTrace();
            throw e; 
        }
    }
    
    public static String httpGet(String urlStr) throws Exception {      
        try {
            URL url = new URL(urlStr);
//            System.out.print("+++++++++++url" + url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestProperty("Authorization", "Bearer " + UpdateUserUtils.getToken("http://172.16.8.103:8082", "admin", "admin"));
            String respone = "";
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                respone = IOUtils.convertStreamToString(conn.getInputStream());
            } else {
                throw new Exception(conn.getResponseMessage());
            }
            conn.disconnect();
//            System.out.print("+++++++++++respone" + respone);
            return respone;
        } catch (Exception e) {
            e.printStackTrace();
            throw e; 
        }
    }
    
    public static String httpPut(String urlStr) throws Exception {      
        try {
            URL url = new URL(urlStr);
//            System.out.print("+++++++++++url" + url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            String respone = "";
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                respone = IOUtils.convertStreamToString(conn.getInputStream());
            } else {
                throw new Exception(conn.getResponseMessage());
            }
            conn.disconnect();
//            System.out.print("+++++++++++respone" + respone);
            return respone;
        } catch (Exception e) {
            e.printStackTrace();
            throw e; 
        }
    }
    
    public static void httpDelete(String urlStr) throws Exception {      
        try {
            URL url = new URL(urlStr);
//            System.out.print("+++++++++++url" + url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK){
                throw new Exception(conn.getResponseMessage());
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            throw e; 
        }
    }
}
