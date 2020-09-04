/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.dev.shkao.web;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import tw.dev.shkao.persistence.BaseEntity;
import tw.dev.shkao.persistence.BaseEntityManager;

/**
 *
 * @author kengao
 */
public class EntityConverter implements Converter {

    private final BaseEntityManager baseManagerBean;

    public EntityConverter(BaseEntityManager baseManagerBean) {
        super();
        this.baseManagerBean = baseManagerBean;
    }
    
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if(baseManagerBean==null)
            throw new RuntimeException("尚未設定 entityManager");
        
        if(value == null)
            return null;
        
        try{
            Integer id = Integer.parseInt(value);
            return baseManagerBean.getByPrimaryKey( id );
        }catch(RuntimeException ex){
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if( value!=null )
            return ((BaseEntity) value).getId().toString();
        return null;
    }
    
}
