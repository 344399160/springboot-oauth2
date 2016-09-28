package com.scistor.tab.auth.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import org.apache.commons.io.IOUtils;

@Configuration
public class UpdateUserUtils {
   
    public static Map<String, Object> getUser(String systemServiceUrl, String userName, String password) throws Exception {
        URL getUserUrl = new URL(systemServiceUrl + "/users");
        HttpURLConnection connection = (HttpURLConnection) getUserUrl.openConnection();
        connection.setRequestProperty("Authorization", "Bearer " + getToken(systemServiceUrl, userName, password));
        InputStream in = connection.getInputStream();
        String user = IOUtils.toString(in);
        return new ObjectMapper().readValue(user, Map.class);
    }
    
    public static String getToken(String url, String userName, String password) {        
        try {
            URL tokenUrl = new URL(url + "/oauth/token" + "?password=" + password + "&username=" + userName + "&grant_type=password");
            HttpURLConnection conn = (HttpURLConnection) tokenUrl.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Basic d2ViX3NlcnZlcjpkMjNiZWRjNC1hODU2LTRjY2MtOWMyMi02ODY3NWRjNWVlMjc=");
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream in = conn.getInputStream();
                String token = IOUtils.toString(in);
                Map<String, String> tokenMap = new ObjectMapper().readValue(token, Map.class);
                return tokenMap.get("access_token");
            } else {
                System.out.println("responese: " + conn.getResponseMessage());
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;   
        
    }
}
