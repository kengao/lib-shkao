/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.dev.shkao.web;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import tw.dev.shkao.persistence.BaseEntity;
import tw.dev.shkao.persistence.BaseEntityManager;

/**
 *
 * @author kengao
 */
public abstract class AbstractEntityEditorPageBean<T extends BaseEntity> extends BasePageBean {
     
    List<T> dataList;

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }
    
    
    public Set<String> getColumns(){
        
        Set<String> getSet = new HashSet();
        Set<String> setSet = new HashSet();
        
        for(Method m : getEntityClass().getMethods()){
            String name = m.getName();
            
            if(name.startsWith("get")){
                name = name.replaceFirst("get", "");
                if(!name.isEmpty()){
                    getSet.add(name);
                }
            }
            if(name.startsWith("set")){
                name = name.replaceFirst("set", "");
                if(!name.isEmpty()){
                    setSet.add(name);
                }
            }
        }
        getSet.retainAll(setSet);
        
        Set<String> resultSet = new HashSet<>();
        
        for(String s : getSet){
            s = s.replaceFirst(s.substring(0,1), s.substring(0,1).toLowerCase());
            resultSet.add(s);
        }
        
        return resultSet;
    }
    
    public void add(){
        
        BaseEntity object;
        try {
            
            object = getEntityClass().newInstance();
            getEntityManagerBean().create(object);
            
        } catch (InstantiationException | IllegalAccessException ex) {
            logError(ex);
            error(ex);
        }
            
        
            
        loadData();
    }
    
    public void delete(T data){
        
        getEntityManagerBean().remove(data);
        
        loadData();
    }
    
    public void edit(T data){
        
        getEntityManagerBean().edit(data);
        
        loadData();
    }
    
    @Override
    protected void initialize() {
        super.initialize();
        
        loadData();
    }
    
    public void loadData(){
        dataList = getEntityManagerBean().findAll();
    }
    
    protected abstract BaseEntityManager getEntityManagerBean();
    
    protected abstract Class<T> getEntityClass();
}
