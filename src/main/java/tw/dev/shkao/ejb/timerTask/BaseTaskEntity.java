/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.dev.shkao.ejb.timerTask;

import java.util.Date;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import tw.dev.shkao.persistence.BaseEntity;

/**
 *
 * @author kengao
 */
@MappedSuperclass
public abstract class BaseTaskEntity extends BaseEntity {
    private String name;
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdateTime = new Date();
    private Boolean enabled = Boolean.TRUE;
    
    private String jobInfo = "0 0 0 * * * *";
    private String timezone = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getJobInfo() {
        return jobInfo;
    }

    public void setJobInfo(String jobInfo) {
        this.jobInfo = jobInfo;
    }
    
    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
    
    
}
