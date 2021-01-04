/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.dev.shkao.ejb;

import java.util.ArrayList;
import java.util.List;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 *
 * @author kengao
 */
public class BeanProvider { 
    
    String applicationName;
    
    List<String> moduleNameList = new ArrayList<>();
    
    public BeanProvider(){
        autoLoadProp();
    }

    private void autoLoadProp(){
        try{
            InitialContext c = new InitialContext();
            this.applicationName = (String)c.lookup("java:app/AppName");
            
            NamingEnumeration<NameClassPair> list = c.list("java:global/"+applicationName );
            while (list.hasMoreElements()) {
                NameClassPair next = list.next();
                String name = next.getName();
                moduleNameList.add(name);
            }
            
        }catch(NamingException ex){
        }
        
        System.out.println("applicationName = " + applicationName + " moduleNameList = " + moduleNameList );
    }

    public Object getBean(Class c){
        return getBean(c.getSimpleName());
    }
    
    public Object getBean(String name){
        
        for(String moduleName : moduleNameList){
            try {
                InitialContext c = new InitialContext();
                Object theFacade = c.lookup("java:global/" + applicationName + "/" + moduleName + "/" + name );
                return theFacade;
            } catch (NamingException ex) {
            }
        }
        
        return null;
    }

}

