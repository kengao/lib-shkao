/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.dev.shkao.faces.application;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import tw.dev.shkao.util.WebUtil;

/**
 *
 * @author kengao
 */
public class FacesApplicationHandler implements PhaseListener {

    private static final Logger LOGGER = tw.dev.shkao.util.log.Logger.APPLICATION.getLogger();
    
    @Override
    public void afterPhase(PhaseEvent event) {
        PhaseId phaseId = event.getPhaseId();
        FacesContext context = event.getFacesContext();
        UIViewRoot viewRoot = context.getViewRoot();
        
        
    }

    @Override
    public void beforePhase(PhaseEvent event) {
        
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
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
}
