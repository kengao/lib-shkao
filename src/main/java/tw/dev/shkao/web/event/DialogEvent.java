/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.dev.shkao.web.event;

import java.util.EventObject;
import tw.dev.shkao.web.BaseDialogBean;

/**
 *
 * @author kengao
 */
public class DialogEvent extends EventObject {
    
    private Object returnStatus = null ;
    private Object returnValue = null ;

    public DialogEvent(BaseDialogBean source) {
        
        this(source, null, null);

    }
    
    public DialogEvent(BaseDialogBean source, Object returnStatus) {
        
        this(source, returnStatus, null);
        
    }
    
    public DialogEvent(BaseDialogBean source, Object returnStatus, Object returnValue) {
        
        super(source);
        
        this.returnStatus = returnStatus ;
        this.returnValue = returnValue ;

    }
 
    public <T> T getReturnStatus() {
        return (T)returnStatus;
    }
 
    public <T> T getReturnValue() {
        return (T)returnValue;
    }
}
