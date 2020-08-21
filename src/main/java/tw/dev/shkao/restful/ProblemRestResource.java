/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.dev.shkao.restful;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;


public class ProblemRestResource {
    int statusCode;
    WebApplicationException problem;

    public ProblemRestResource(Response.Status status, WebApplicationException problem) {
        this(status.getStatusCode(), problem) ;
    }

    public ProblemRestResource(int statusCode, WebApplicationException problem) {
        this.statusCode = statusCode;
        this.problem = problem;
    }

    public ProblemRestResource(Response.Status status, String error) {
        this(status, new WebApplicationException(error)) ;
    }

    public ProblemRestResource(int statusCode, String error) {
        this(statusCode, new WebApplicationException(error)) ;
    }

    public static ProblemRestResource getInstance(Response.Status status, String error) {
        return new ProblemRestResource(status, error);
    }

    public static ProblemRestResource getInstance(int statusCode, String error) {
        return new ProblemRestResource(statusCode, error);
    }

    protected Response getResponse(){
        
        ResponseBuilder responseBuilder = Response.status(statusCode);
        responseBuilder = responseBuilder.type(MediaType.APPLICATION_JSON);
        responseBuilder.entity(problem.getMessage());
        return responseBuilder.build();
    }
    
    // -------------------------------------------------------------------------
    @GET
    public Response doGet() {
        return getResponse();
    }    
    
    @GET
    @Path("{any}")
    public Response doGetWithPath() {
        return getResponse();
    }

    @POST
    public Response doPost() {
        return getResponse();
    }

    @PUT
    public Response doPut() {
        return getResponse();
    }

    @DELETE
    public Response doDelete() {
        return getResponse();
    }
}
