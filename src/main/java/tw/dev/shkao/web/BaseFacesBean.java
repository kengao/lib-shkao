/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.dev.shkao.web;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.NavigationHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.primefaces.PrimeFaces;

/**
 *
 * @author kengao
 */
public class BaseFacesBean implements Serializable{
    
    private static final Logger LOGGER = tw.dev.shkao.util.log.Logger.WEB.getLogger();
    
    @PostConstruct
    private void postConstruct() {
        initialize();
    }

    @PreDestroy
    private void preDestroy() {
        destroy();
    }
    
    protected void destroy(){
    }
    
    protected void initialize() {
    }
    
    public void noop(){};
    
    public <T extends UIComponent> T findComponent(String id) {
        return findComponent(getFacesContext().getViewRoot(), id);
    }

    public <T extends UIComponent> T findComponent(UIComponent parent, String id) {
        T component = (T) parent.findComponent(id);

        if (component != null) {
            return component;
        }

        for (Iterator<UIComponent> iter = parent.getChildren().iterator(); component == null && iter.hasNext();) {
            UIComponent child = iter.next();
            component = findComponent(child, id);
        }

        return component;
    }
    
    public void update(String... componentIds) {

        if (componentIds == null || componentIds.length == 0) {
            return;
        }

        update(Arrays.asList(componentIds));
    }
    
    public void update(Collection<String> componentIdList) {

        List<String> list = new ArrayList<>();

        for (String id : componentIdList) {
            UIComponent uiComponent = findComponent(id);
            if (uiComponent != null) {
                list.add(uiComponent.getClientId());
            }
        }
        
        PrimeFaces pf = PrimeFaces.current();
        pf.ajax().update(list);

    }

    public void update(String componentId) {

        UIComponent uiComponent = findComponent(componentId);
        if (uiComponent == null) {
            return;
        }

        PrimeFaces pf = PrimeFaces.current();
        pf.ajax().update(uiComponent.getClientId());

    }
    
    
    public void execute(String javaScriptCommand) {
        PrimeFaces pf = PrimeFaces.current();
        pf.executeScript(javaScriptCommand);
    }
    
    protected ExternalContext getExternalContext() {

        return FacesContext.getCurrentInstance().getExternalContext();

    }
 
    protected FacesContext getFacesContext() {

        return FacesContext.getCurrentInstance();

    } 
    
    protected Lifecycle getLifecycle() {

        String lifecycleId = getExternalContext().getInitParameter("javax.faces.LIFECYCLE_ID");  //NOI18N
        if (lifecycleId == null || lifecycleId.isEmpty()) {
            lifecycleId = LifecycleFactory.DEFAULT_LIFECYCLE;
        }

        LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
        return lifecycleFactory.getLifecycle(lifecycleId);

    }
 
    protected Map<String, Object> getRequestMap() {

        return getExternalContext().getRequestMap();

    }

    protected Map<String, String> getRequestParameterMap() {

        return getExternalContext().getRequestParameterMap();

    }
    
    protected Map<String, String[]> getRequestParameterValuesMap() {

        return getExternalContext().getRequestParameterValuesMap();

    }

    protected Map<String, Object> getSessionMap() {

        return getExternalContext().getSessionMap();

    }

    public Map<String, Object> getViewMap() {

        UIViewRoot viewRoot = getFacesContext().getViewRoot();
        return viewRoot.getViewMap(true);

    }

    protected Map<String, Object> getApplicationMap() {

        return getExternalContext().getApplicationMap();

    }
    
    public Object getApplicationAttribute(String attrName) {
        return getApplicationMap().get(attrName);
    }

    public void setApplicationAttribute(String attrName, Object value) {
        if (value != null) {
            getApplicationMap().put(attrName, value);
        } else {
            getApplicationMap().remove(attrName);
        }
    }

    public Object removeApplicationAttribute(String attrName) {
        return getApplicationMap().remove(attrName);
    }

    public Object getSessionAttribute(String attrName) {
        return getSessionMap().get(attrName);
    }

    public void setSessionAttribute(String attrName, Object value) {
        if (value != null) {
            getSessionMap().put(attrName, value);
        } else {
            getSessionMap().remove(attrName);
        }
    }

    public Object removeSessionAttribute(String attrName) {
        return getSessionMap().remove(attrName);
    }

    public Object getViewAttribute(String attrName) {
        return getViewMap().get(attrName);
    }

    public void setViewAttribute(String attrName, Object value) {
        if (value != null) {
            getViewMap().put(attrName, value);
        } else {
            getViewMap().remove(attrName);
        }
    }

    public Object removeViewAttribute(String attrName) {
        return getViewMap().remove(attrName);
    }

    public Object getRequestAttribute(String attrName) {
        return getRequestMap().get(attrName);
    }

    public void setRequestAttribute(String attrName, Object value) {
        if (value != null) {
            getRequestMap().put(attrName, value);
        } else {
            getRequestMap().remove(attrName);
        }
    }

    public Object removeRequestAttribute(String attrName) {
        return getRequestMap().remove(attrName);
    }

    protected HttpSession getSession() {
        return getSession(false);
    }

    protected HttpSession getSession(boolean create) {
        return (HttpSession) getExternalContext().getSession(create);
    }

    protected HttpServletRequest getRequest() {
        return (HttpServletRequest) getExternalContext().getRequest();
    }

    protected HttpServletResponse getResponse() {
        return (HttpServletResponse) getExternalContext().getResponse();
    }
    
    protected void redirect(String url) {

        if (getFacesContext().getResponseComplete()) {
            return;
        }

        try {
            getExternalContext().redirect(url);
            getFacesContext().responseComplete();
        } catch (IOException ex) {
            logWarning(ex);
        }
        
    }
    
    protected NavigationHandler getNavigationHandler() {

        return getApplication().getNavigationHandler();

    }

    protected void navigate(String outCome) {

        NavigationHandler nh = getNavigationHandler();
        nh.handleNavigation(getFacesContext(), null, outCome);

    }
    
    protected Application getApplication() {

        return FacesContext.getCurrentInstance().getApplication();

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
    

    protected void raiseMessage(FacesMessage message) {
        raiseMessage((String) null, message);
    }

    protected void raiseMessage(UIComponent component, FacesMessage message) {
        raiseMessage(component.getClientId(getFacesContext()), message);
    }

    protected void raiseMessage(String clientId, FacesMessage message) {
        getFacesContext().addMessage(clientId, message);
    }

    protected void info(String summary) {

        raiseMessage(new FacesMessage(FacesMessage.SEVERITY_INFO, summary, null));

    }

    protected void info(String summary, String detail) {
        raiseMessage(new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail));
    }

    protected void info(UIComponent component, String summary) {

        raiseMessage(component, new FacesMessage(FacesMessage.SEVERITY_INFO, summary, null));

    }

    protected void warn(String summary) {

        raiseMessage(new FacesMessage(FacesMessage.SEVERITY_WARN, summary, null));

    }

    protected void warn(UIComponent component, String summary) {

        raiseMessage(component, new FacesMessage(FacesMessage.SEVERITY_WARN, summary, null));

    }

    protected void warn(String summary, String detail) {
        raiseMessage(new FacesMessage(FacesMessage.SEVERITY_WARN, summary, detail));
    }

    protected void error(String summary) {
        raiseMessage(new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, null));
    }

    protected void error(String summary, String detail) {
        raiseMessage(new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail));
    }

    protected void error(Throwable throwable, String summary) {
       error(throwable, summary, false) ;
    }
    
    protected void error(Throwable throwable) {
       error(throwable, null, false) ;
    }

    protected void error(Throwable throwable, boolean logMessage) {
        error(throwable, null, logMessage);
    }

    protected void error(Throwable throwable, String summary, boolean logMessage) {
        StringBuilder sbSummary = new StringBuilder() ;
        
        if(summary != null) {
            sbSummary.append(summary)
                    .append(": ")
                    .append(throwable.getMessage()) ;
        } else {
            sbSummary.append(throwable.getMessage()) ;
        }
        
        raiseMessage(new FacesMessage(FacesMessage.SEVERITY_ERROR, sbSummary.toString(), null));
        
        if(logMessage) {
            logError(throwable);
        }
    }
    
    protected void error(List<String> summaryList) {

        for (String summary : summaryList) {
            raiseMessage(new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, null));
        }

    }

    protected void error(UIComponent component, String summary) {

        raiseMessage(component, new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, null));

    }

    protected void fatal(String summary) {

        raiseMessage(new FacesMessage(FacesMessage.SEVERITY_FATAL, summary, null));

    }

    protected void fatal(String summary, String detail) {
        raiseMessage(new FacesMessage(FacesMessage.SEVERITY_FATAL, summary, detail));
    }

    protected void fatal(Throwable throwable) {
        fatal(throwable, false);
    }

    protected void fatal(Throwable throwable, boolean log) {
        raiseMessage(new FacesMessage(FacesMessage.SEVERITY_FATAL, throwable.getMessage(), null));
        
        if(log) {
            logError(throwable);
        }
    }
    
}
