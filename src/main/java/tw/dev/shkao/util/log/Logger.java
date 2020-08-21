/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.dev.shkao.util.log;

/**
 *
 * @author kengao
 */
public enum Logger {

    PERSISTENCE("persistence"),
    EJB("ejb"),
    WEB("web"),
    RESTFUL("restful");

    private static final String DEFAULT_LOGGER_RESOURCES = "LogStrings";
    public static final String FACES_LOGGER_NAME_PREFIX = "tw.dev.shkao";
    private String loggerName;
    private String resourceBundleName ;

    Logger(String loggerName) {
        this(loggerName, DEFAULT_LOGGER_RESOURCES) ;
    }
    
    Logger(String loggerName, String resourceBundleName) {
        
        if(resourceBundleName == null || resourceBundleName.isEmpty()) {
            resourceBundleName = DEFAULT_LOGGER_RESOURCES ;
        }
        
        this.loggerName = String.format("%s.%s", FACES_LOGGER_NAME_PREFIX, loggerName) ;
        this.resourceBundleName = String.format("%s.%s.%s", FACES_LOGGER_NAME_PREFIX, loggerName, resourceBundleName) ;
        
    }

    public String getLoggerName() {
        
        return loggerName;
        
    }

    public String getResourcesName() {
        
        return resourceBundleName;
        
    }

    public java.util.logging.Logger getLogger() {
        
        return java.util.logging.Logger.getLogger(loggerName, resourceBundleName);
        
    }
}

