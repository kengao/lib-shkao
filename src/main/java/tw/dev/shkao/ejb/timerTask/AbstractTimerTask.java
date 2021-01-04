/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.dev.shkao.ejb.timerTask;

import java.util.Date;
import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.ejb.Schedule;
import javax.ejb.ScheduleExpression;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import tw.dev.shkao.ejb.BaseHelperBean;

/**
 *
 * @author kengao
 */
public abstract class AbstractTimerTask extends BaseHelperBean {

    @Resource 
    protected TimerService timerService;

    protected abstract String getTaskName();
    public abstract boolean businessLogic(Timer timer);
    protected abstract BaseTaskEntity getTaskEntity(String taskName);
    
    @Override
    protected void initialize() {
        super.initialize();
        System.out.print(getTaskName() +" is initialized");
        maintain();
    }
    
    protected TimerService getTimerService(){
        return timerService;
    };
    
    @Timeout
    public void timeout(Timer timer) {
        BaseTaskEntity entity = getTaskEntity(getTaskName());
        if( (entity==null || !Boolean.TRUE.equals(entity.getEnabled())) && timer!=null){
            return;
        }
        boolean success = businessLogic(timer);
        //if(success) System.out.print(getTaskName() + ".businessLogic() is executed.");
        if(success) postTimerExecuted();
    }
    
    protected void postTimerExecuted(){};
    
    @Schedule(hour = "*", minute = "*", second = "0", persistent=false, info="maintainTimer")
    protected void maintain(){
        BaseTaskEntity entity = getTaskEntity(getTaskName());
        if(entity==null)
            return;
        
        boolean dataisChanged = false;
        boolean enabled = false;
        BaseTaskEntity taskData = entity;

        if( getLastUpdated()==null || !getLastUpdated().equals(taskData.getLastUpdateTime()) ){
            dataisChanged = true;
            enabled = Boolean.TRUE.equals(taskData.getEnabled());
        }
        
        if(dataisChanged){
            
            for(Timer t : getTimerService().getTimers()){
                if( "maintainTimer".equals(t.getInfo()) || !getTaskName().equals(t.getInfo()) )
                    continue;
                t.cancel();
            }
            if(!enabled){
                return;
            }
            
            System.out.print(getTaskName() + ".maintain() creates new timer.");
            ScheduleExpression expr = new ScheduleExpression();
            expr.timezone(taskData.getTimezone());
            
            String jobInfo = taskData.getJobInfo();
            String[] jobArray = jobInfo.split(" ");
            
            if(jobArray.length>6)
                expr.year(jobArray[6]);
            
            expr.dayOfWeek(jobArray[5]);
            expr.month(jobArray[4]);
            expr.dayOfMonth(jobArray[3]);
            expr.hour(jobArray[2]);
            expr.minute(jobArray[1]);
            expr.second(jobArray[0]);

            TimerConfig tc = new TimerConfig();
            tc.setPersistent(false);
            tc.setInfo( getTaskName() );
            
            try{
                getTimerService().createCalendarTimer(expr, tc);
                setLastUpdated( taskData.getLastUpdateTime() );
            }catch(IllegalArgumentException | IllegalStateException | EJBException ex){
                logError(ex);
            }
            
        }
    }
    
    @Override
    protected void release(){
        getTimerService().getTimers().forEach((t) -> {
            t.cancel();
        });
    }
    
    protected abstract Date getLastUpdated();
    protected abstract void setLastUpdated(Date lastUpdated);
    
}
