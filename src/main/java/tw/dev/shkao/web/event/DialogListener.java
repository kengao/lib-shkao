/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.dev.shkao.web.event;

import java.util.EventListener;

/**
 *
 * @author kengao
 */
public class DialogListener implements EventListener {

    public void dialogOpened(DialogEvent e){
        
    }

    public boolean dialogClosing(DialogEvent e){
        return true;
    }

    public void dialogClosed(DialogEvent e){
        
    }
}