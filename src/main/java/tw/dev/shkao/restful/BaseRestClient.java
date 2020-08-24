/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.dev.shkao.restful;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import tw.dev.shkao.util.JsonUtil;
import tw.dev.shkao.util.WebUtil;

/**
 *
 * @author kengao
 */
public class BaseRestClient {
    
    private static final Logger LOGGER = tw.dev.shkao.util.log.Logger.RESTFUL.getLogger();
    
    public static ObjectMapper getObjectMapper() {
        return JsonUtil.getObjectMapper();
    }
    
    private final URL baseUrl;
    
    public BaseRestClient(String baseUri) {
        baseUrl = WebUtil.getURL(baseUri);

        if (baseUrl == null) {
            throw new IllegalArgumentException();
        }
    }

    protected <T> T doGet(WebTarget webTarget, Class<T> entityClass){

        if (entityClass == null) {
            throw new NullPointerException();
        }

        if (webTarget == null) {
            throw new NullPointerException();
        }

        Response response = doGet(webTarget);
        
        Response.Status status = Response.Status.fromStatusCode(response.getStatus());
        if (Response.Status.Family.SUCCESSFUL.equals(status.getFamily())) {
            return response.readEntity(entityClass);
        }
        
        return null;
    }
    
    protected Response doGet(WebTarget webTarget) {
        if (webTarget == null) {
            throw new NullPointerException();
        }
        Invocation.Builder invocationBuilder = webTarget.request();
        return invocationBuilder.buildGet().invoke();
    }
    
    protected Response doPost(WebTarget webTarget, MultivaluedMap<String, String> formData) {
        if (webTarget == null) {
            throw new NullPointerException();
        }
        Invocation.Builder invocationBuilder = webTarget.request();
        return invocationBuilder.buildPost(Entity.form(formData)).invoke();
    }
    
    protected <T> T doPost(WebTarget webTarget, MultivaluedMap<String, String> formData, Class<T> entityClass){

        if (entityClass == null) {
            throw new NullPointerException();
        }

        if (webTarget == null) {
            throw new NullPointerException();
        }

        Response response = doPost(webTarget, formData);
        
        Response.Status status = Response.Status.fromStatusCode(response.getStatus());
        if (Response.Status.Family.SUCCESSFUL.equals(status.getFamily())) {
            return response.readEntity(entityClass);
        }
        
        return null;
    }
    
    protected Response doDelete(WebTarget webTarget) {
        if (webTarget == null) {
            throw new NullPointerException();
        }
        Invocation.Builder invocationBuilder = webTarget.request();
        return invocationBuilder.buildDelete().invoke();
    }
    
    protected Response doPut(WebTarget webTarget, MultivaluedMap<String, String> formData) {
        if (webTarget == null) {
            throw new NullPointerException();
        }
        Invocation.Builder invocationBuilder = webTarget.request();
        return invocationBuilder.buildPut(Entity.form(formData)).invoke();
    }
    
    protected WebTarget getWebTarget() {
        return WebUtil.getWebTarget(baseUrl);
    }
    
    protected WebTarget queryParams(WebTarget wt, MultivaluedMap<String, String> map) {

        if (wt == null) {
            throw new IllegalArgumentException();
        }

        if (map == null || map.isEmpty()) {
            return wt;
        }

        for (MultivaluedMap.Entry<String, List<String>> entry : map.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            String[] valueArray = values.toArray(new String[values.size()]);
            wt = wt.queryParam(key, valueArray);
        }

        return wt;
    }
    
    protected void logInfo(Throwable throwable) {
        log(Level.INFO, null, throwable);
    }
    
    protected void logInfo(String message) {
        log(Level.INFO, message);
    }

    protected void logInfo(String message, Object param) {
        log(Level.INFO, message, param);
    }

    protected void logInfo(String message, Object[] params) {
        log(Level.INFO, message, params);
    }

    protected void logError(Throwable throwable) {
        log(Level.SEVERE, null, throwable);
    }
    
    protected void logError(String message) {
        log(Level.SEVERE, message);
    }

    protected void logError(String message, Object param) {
        log(Level.SEVERE, message, param);
    }

    protected void logError(String message, Object[] params) {
        log(Level.SEVERE, message, params);
    }

    protected void logError(String message, Throwable throwable) {
        log(Level.SEVERE, message, throwable);
    }

    protected void logWarning(Throwable throwable) {
        log(Level.WARNING, null, throwable);
    }
    
    protected void logWarning(String message) {
        log(Level.WARNING, message);
    }

    protected void logWarning(String message, Object param) {
        log(Level.WARNING, message, param);
    }

    protected void logWarning(String message, Object[] params) {
        log(Level.WARNING, message, params);
    }

    protected void logWarning(String message, Throwable throwable) {
        log(Level.WARNING, message, throwable);
    }

    protected void log(Level logLevel, String message) {
        if (LOGGER.isLoggable(logLevel)) {
            LOGGER.log(logLevel, message);
        }
    }

    protected void log(Level logLevel, String message, Object param) {
        if (LOGGER.isLoggable(logLevel)) {
            LOGGER.log(logLevel, message, param);
        }
    }

    protected void log(Level logLevel, String message, Object[] params) {
        if (LOGGER.isLoggable(logLevel)) {
            LOGGER.log(logLevel, message, params);
        }
    }

    protected void log(Level logLevel, String message, Throwable throwable) {
        if (LOGGER.isLoggable(logLevel)) {
            LOGGER.log(logLevel, message, throwable);
        }
    }

    public static boolean isUrlConnectable(URL url) {
        if (url == null) {
            throw new NullPointerException();
        }

        try {
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(100);
            conn.connect();
            if (conn instanceof HttpURLConnection) {
                ((HttpURLConnection) conn).disconnect();
            } else {
                conn.getInputStream().close();
            }

            conn = null;
        } catch (ConnectException | SocketTimeoutException ex) {
            return false;
        } catch (IOException ex) {
            return false;
        }

        return true;
    }
}
