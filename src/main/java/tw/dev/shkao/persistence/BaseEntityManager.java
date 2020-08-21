/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.dev.shkao.persistence;

import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

/**
 *
 * @author kengao
 */
 public interface BaseEntityManager<TYPE> {

    void create(TYPE entity);

    void edit(TYPE entity);

    void remove(TYPE entity);

    void remove(Integer primaryKey);

    boolean reload(TYPE entity);

    boolean isExist(Integer key);

    int count();
    
    List<TYPE> findAll();

    List<TYPE> find(CriteriaQuery<TYPE> cq);
    
    List<TYPE> findBy(String columnName, Object... values);

    TYPE getBy(String columnName, Object value);
    
    TYPE get(CriteriaQuery<TYPE> cq);

    CriteriaBuilder getCriteriaBuilder() ;
    
    CriteriaQuery<TYPE> createCriteriaQuery() ;

    TYPE getByPrimaryKey(Integer value);
}

