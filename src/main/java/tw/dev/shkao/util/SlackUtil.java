/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.dev.shkao.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import static tw.dev.shkao.restful.BaseRestResource.getObjectMapper;

/**
 *
 * @author kengao
 */
public class SlackUtil {
    
    public static String post(String message, String url, String username, String icon_emoji){
        
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(url);

        Map<String, String> restData = new HashMap<>();
        restData.put("text", message);
        
        if(username!=null){ 
            restData.put("username", username);
        }
        
        if(icon_emoji!=null){ 
            restData.put("icon_emoji", icon_emoji);
        }
        
        String jsonStr;
        try {
            jsonStr = getObjectMapper().writeValueAsString(restData);
            String result = webTarget.request(MediaType.APPLICATION_FORM_URLENCODED_TYPE).accept(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(jsonStr)).readEntity(String.class);
            return result;
            
        } catch (JsonProcessingException ex) {
            Logger.getLogger(SlackUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "";
    
    }
    
    public static String post(String message, String url, String username){
        return post(message, url, username, null);
    }
    
    public static String post(String message, String url){
        return post(message, url, null, null);
    }
}
