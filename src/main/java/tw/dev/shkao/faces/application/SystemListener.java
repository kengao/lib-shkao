/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.dev.shkao.faces.application;

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PostConstructApplicationEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;

/**
 *
 * @author kengao
 */
public class SystemListener implements SystemEventListener {

    @Override
    public void processEvent(SystemEvent event) throws AbortProcessingException {
        if (event instanceof PostConstructApplicationEvent) {

            applicationPostConstruct((PostConstructApplicationEvent) event);
        }
    }

    @Override
    public boolean isListenerForSource(Object source) {
        
        if (source instanceof Application) {
            return true;
        } else return source instanceof UIViewRoot;
        
    }
    
    private void applicationPostConstruct(PostConstructApplicationEvent event) {

        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        
        FacesApplicationHandler facesApplicationHandler = new FacesApplicationHandler();
        getLifecycle().addPhaseListener(facesApplicationHandler);

    }
    
    private static Lifecycle getLifecycle() {

        String lifecycleId = LifecycleFactory.DEFAULT_LIFECYCLE; // FIXME - override?
        LifecycleFactory factory = (LifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
        Lifecycle lifecycle = factory.getLifecycle(lifecycleId);

        return lifecycle;

    }
    
}
