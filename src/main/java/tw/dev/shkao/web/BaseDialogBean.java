/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.dev.shkao.web;

import java.util.HashMap;
import java.util.Map;
import org.primefaces.PrimeFaces;
import org.primefaces.component.dialog.Dialog;
import tw.dev.shkao.web.event.DialogEvent;
import tw.dev.shkao.web.event.DialogListener;

/**
 *
 * @author kengao
 */
public abstract class BaseDialogBean extends BaseFacesBean{
    
    protected DialogListener openerDialogBeanListener;
    
    protected String dialogTitle;
    
    public String getDialogTitle() {
        return dialogTitle;
    }

    public void setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
    }
    
    public void showDialog(){
        showDialog((Map<String, Object>) null);
    }

    public void showDialog(Map<String, Object> dialogParameters){
        showDialog(dialogParameters, null);
    }
    
    public void showDialog(DialogListener openerDialogListener){
        showDialog(null, openerDialogListener);
    }

    public void showDialog(Map<String, Object> dialogParameters, DialogListener openerDialogListener){
        Dialog dialog = getDialogComponent();

        if (dialog == null) {
            return;
        }

        if (dialogParameters == null) {
            dialogParameters = new HashMap<>();
        }
        
        // ---------------------------------------------------------------------
        try {
            if (!preDialogOpening(dialogParameters)) {
                return;
            }
        } catch (Throwable t) {
            logError(null, t);
            error(t.getMessage());
            return;
        }

        // ---------------------------------------------------------------------
        this.openerDialogBeanListener = openerDialogListener;

        PrimeFaces pf = PrimeFaces.current();
        if (pf != null) {
            pf.executeScript(String.format("PF('%s').show()", dialog.getWidgetVar()));
            pf.ajax().update(dialog.getClientId());
        } else {
            dialog.setVisible(true);
        }

        // ---------------------------------------------------------------------
        try {
            postDialogOpened(dialogParameters);
        } catch (Throwable t) {
            logError(null, t);
            error(t.getMessage());
            return;
        }

        try {
            fireDialogOpened();
        } catch (Throwable t) {
            logError(null, t);
            error(t.getMessage());
        }
    }
    
    public boolean hideDialog(Object returnStatus) {

        return hideDialog(returnStatus, null);

    }

    public boolean hideDialog(Object returnStatus, Object returnValue) {

        try {
            preDialogClosing();
        } catch (Throwable t) {
            logError(null, t);
            error(t.getMessage());
            return false;
        }

        try {

            if (!fireDialogClosing(returnStatus, returnValue)) {
                return false;
            }

        } catch (Throwable t) {
            logError(null, t);
            error(t.getMessage());
            return false;
        }

        Dialog dialog = getDialogComponent();

        PrimeFaces pf = PrimeFaces.current();
        if (pf != null) {
            pf.executeScript(String.format("PF('%s').hide()", dialog.getWidgetVar()));
            pf.ajax().update(dialog.getClientId());
        } else {
            dialog.setVisible(false);
        }

        try {

            fireDialogClosed(returnStatus, returnValue);

        } catch (Throwable t) {
            logError(null, t);
            error(t.getMessage());
            return false;
        }

        this.openerDialogBeanListener = null;
        return true;

    }

    // -------------------------------------------------------------------------
    public boolean preDialogOpening(Map<String, Object> dialogParameters) {
        return true;
    }

    public void postDialogOpened(Map<String, Object> dialogParameters) {
        
    }

    public void preDialogClosing() {
        
    }
    protected void fireDialogClosing(Object returnStatus) {

        fireDialogClosing(returnStatus, null);

    }

    protected boolean fireDialogClosing(Object returnStatus, Object returnValue) {

        if (openerDialogBeanListener == null) {
            return true;
        }

        return openerDialogBeanListener.dialogClosing(new DialogEvent(this, returnStatus, returnValue));

    }

    protected void fireDialogClosed(Object returnStatus) {

        fireDialogClosed(returnStatus, null);

    }

    protected void fireDialogClosed(Object returnStatus, Object returnValue) {

        if (openerDialogBeanListener == null) {
            return;
        }

        openerDialogBeanListener.dialogClosed(new DialogEvent(this, returnStatus, returnValue));

    }

    public void setDialogBeanListener(DialogListener opoenerDialogListener) {
        this.openerDialogBeanListener = opoenerDialogListener;
    }
    
    protected void fireDialogOpened() {

        if (openerDialogBeanListener == null) {
            return;
        }

        openerDialogBeanListener.dialogOpened(new DialogEvent(this));

    }
    
    protected abstract Dialog getDialogComponent();
}
