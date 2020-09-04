/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.dev.shkao.restful;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import tw.dev.shkao.util.JsonUtil;

/**
 *
 * @author kengao
 */

public class BaseRestResource {

    private static final Logger LOGGER = tw.dev.shkao.util.log.Logger.RESTFUL.getLogger();
    
    private static final SimpleDateFormat SDF_YYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");

    private static final Set<String> LOCAL_ADDRESSES_SET = new HashSet<String>();

    @Context
    protected HttpHeaders headers;
    @Context
    protected HttpServletRequest request;
    @Context
    protected UriInfo uriInfo;
     
    static {
        try {
            LOCAL_ADDRESSES_SET.add(InetAddress.getLocalHost().getHostAddress());
            for (InetAddress inetAddress : InetAddress.getAllByName("localhost")) {
                LOCAL_ADDRESSES_SET.add(inetAddress.getHostAddress());
            }
            for(Enumeration<NetworkInterface> eni = NetworkInterface.getNetworkInterfaces(); eni.hasMoreElements(); ) {
                final NetworkInterface ifc = eni.nextElement();
                if(ifc.isUp()) {
                    for(Enumeration<InetAddress> ena = ifc.getInetAddresses(); ena.hasMoreElements(); ) {
                        LOCAL_ADDRESSES_SET.add(ena.nextElement().getHostAddress());
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.logp(Level.SEVERE, BaseRestResource.class.getName(), "", null, e);
        }
        
    }

    protected static boolean isLocalAddress(String address) {
        
        return LOCAL_ADDRESSES_SET.contains(address) ;
    }
    
    protected static boolean isLocalAddress(HttpServletRequest request) {
        return isLocalAddress(request.getRemoteAddr()) ;
    }
    
    public void checkRemoteAddress(String remoteAddress) throws SecurityException{
        if(isLocalAddress(remoteAddress))
            return;
    }
    
    public void checkRemoteAddress() throws SecurityException{
        checkRemoteAddress(request);
    }
    
    public void checkRemoteAddress(HttpServletRequest request) throws SecurityException{
        checkRemoteAddress(request.getRemoteAddr());
    }
    
    protected Response buildStringResponse(String message){

        return buildStringResponse(Response.Status.OK, message);

    }
    
    protected Response buildStringResponse(Status status, String message){

        Response.ResponseBuilder rb;

        rb = Response.status(status)
                .language(Locale.TAIWAN)
                .type(MediaType.TEXT_PLAIN)
                .entity(message);
        return rb.build();

    }
    
    protected Response buildJsonResponse(Object restData){

        return buildJsonResponse(Response.Status.OK, restData);

    }

    protected Response buildJsonResponse(Response.Status status, Object restData){

        if (status == null) {
            throw new NullPointerException();
        }

        return buildJsonResponse(status.getStatusCode(), restData);

    }

    protected Response buildJsonResponse(int status, Object restData){

        if (restData == null) {
            throw new NullPointerException();
        }

        // ---------------------------------------------------------------------
        Response.ResponseBuilder rb;

        try {

            String strResult = getObjectMapper().writeValueAsString(restData);

            rb = Response.status(status)
                    .language(Locale.TAIWAN)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .entity(strResult);

        } catch (JsonProcessingException ex) {
            logError(null, ex);
            return responseException(Response.Status.INTERNAL_SERVER_ERROR, ex);
        }

        return rb.build();

    }
    
    public static ObjectMapper getObjectMapper() {
        return JsonUtil.getObjectMapper();
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
    
    protected Response responseProblem(int code, String error) {
        return ProblemRestResource.getInstance(code, error).getResponse();
    }

    protected Response responseProblem(Response.Status status, String error) {
        return ProblemRestResource.getInstance(status, error).getResponse();
    }

    protected Response responseException(int code, Exception problem) {
        return ProblemRestResource.getInstance(code, problem.getMessage()).getResponse();
    } 

    protected Response responseException(Response.Status status, Exception problem) {
        return responseException(status.getStatusCode(), problem);
    }
}