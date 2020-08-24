/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.dev.shkao.persistence;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

/**
 *
 * @author kengao
 */
@MappedSuperclass
public class BaseEntity implements Serializable {

    protected static final Logger LOGGER = tw.dev.shkao.util.log.Logger.PERSISTENCE.getLogger();
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BaseEntity)) {
            return false;
        }
        BaseEntity other = (BaseEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BaseEntity[ id=" + id + " ]";
    }
    
    
    public void set(String paramName, Object value) {

        if (paramName == null || paramName.isEmpty()) {
            throw new NullPointerException(String.format("%s.get(): paramName can not be null", getClass().getName()));
        }

        Class entityClass = getClass();
        Method method = null;
        
        String methodName_SET = "set" + paramName.toLowerCase();

        for (Method m : entityClass.getMethods()) {

            Class<?>[] paramTypes = m.getParameterTypes();

            if (paramTypes.length != 0) {
                continue;
            }

            if (!methodName_SET.equalsIgnoreCase(m.getName())) {
                continue;
            }

            method = m;
            break;
        }

        if (method == null) {
            return;
        }
        
        try {
            method.invoke(this, value);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

    }

    public Object get(String paramName) {

        if (paramName == null || paramName.isEmpty()) {
            throw new NullPointerException(String.format("%s.get(): paramName can not be null", getClass().getName()));
        }

        Class entityClass = getClass();

        Method method = null;

        String methodName_IS = "is" + paramName.toLowerCase();
        String methodName_GET = "get" + paramName.toLowerCase();

        for (Method m : entityClass.getMethods()) {

            Class<?>[] paramTypes = m.getParameterTypes();

            if (paramTypes.length != 0) {
                continue;
            }

            if (!methodName_IS.equalsIgnoreCase(m.getName()) && !methodName_GET.equalsIgnoreCase(m.getName())) {
                continue;
            }

            method = m;
            break;
        }

        if (method == null) {
            return null;
        }

        Object returnValue = null;

        try {
            returnValue = method.invoke(this);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        return returnValue;
    }
    
    
    @PrePersist
    public void prePersist(){};
    @PostPersist
    public void postPersist(){};
    @PostLoad
    public void postLoad(){};
    @PreUpdate
    public void preUpdate(){};
    @PostUpdate
    public void postUpdate(){};
    @PreRemove
    public void preRemove(){};
    @PostRemove
    public void postRemove(){};
    
}
