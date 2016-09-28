package com.scistor.tab;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class TestPost implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String testValue = "";
	
	@SuppressWarnings("resource")
    public static void main(String[] args) {		
        try {
            URL url = new URL("http://localhost:8080/oauth/token?password=admin&username=admin&grant_type=password");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            String input = "web_server:d23bedc4-a856-4ccc-9c22-68675dc5ee27";
            conn.setRequestProperty("Authorization", "Basic " + new String(Base64.encodeBase64(input.getBytes()), "utf-8"));
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream in = conn.getInputStream();
                String token = IOUtils.toString(in);
                Map<String, String> tokenMap = new ObjectMapper().readValue(token, Map.class);
                System.out.println(tokenMap.get("access_token"));
            } else {
                System.out.println("responese: " + conn.getResponseMessage());
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
}
