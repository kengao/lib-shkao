/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.dev.shkao.persistence;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import tw.dev.shkao.util.log.EmUtility;

/**
 * 
 * @author kengao
 */
public abstract class AbstractFacadeBean<TYPE> implements BaseEntityManager<TYPE> {

    private static final Logger LOGGER = tw.dev.shkao.util.log.Logger.PERSISTENCE.getLogger();
    // -------------------------------------------------------------------------
    private Class<TYPE> entityClass;

    // -------------------------------------------------------------------------
    public AbstractFacadeBean() {

    }

    public AbstractFacadeBean(Class<TYPE> entityClass) {
        this();
        this.entityClass = entityClass;
    }

    protected abstract javax.persistence.EntityManager getEntityManager();

    // --------------------------------------------------- EJB Operation Methods
    @PostConstruct
    private void postConstruct() {

        initialize();
    }

    // ------------------------------------------------------- Protected Methods
    protected void initialize() {
    }

    protected void destroy() {
    }

    // -------------------------------------------------------------------------
    protected final Class<TYPE> getEntityClass() {
        return entityClass;
    }

    /**
     * 取得資料庫表格名稱
     */
    protected final String getSchemaName() {

        String schemaName = null;

        Class<TYPE> entityType = getEntityClass();
        Table table = entityType.getAnnotation(Table.class);
        if (table != null) {
            schemaName = table.schema();
        }

        return schemaName;

    }

    protected String getTableName() {

        String tableName = null;

        Class<TYPE> entityType = getEntityClass();
        Table table = entityType.getAnnotation(Table.class);
        if (table != null) {
            tableName = table.name();
        }

        return tableName;

    }

    protected String getFullTableName() {
        StringBuilder sb = new StringBuilder();

        if (!getSchemaName().isEmpty()) {
            sb.append(getSchemaName()).append(".").append(getTableName());
        } else {
            sb.append(getTableName());
        }

        return sb.toString();
    }
    
    // -------------------------------------------------------------------------
    @Override
    public void create(TYPE entity) {

        getEntityManager().persist(entity);

    }
    
    @Override
    public void edit(TYPE entity) {
        getEntityManager().merge(entity);

    }
    
    @Override
    public void remove(TYPE entity) {
        getEntityManager().remove(getEntityManager().merge(entity));

    }

    @Override
    public void remove(Integer value) {
        TYPE entity = this.getByPrimaryKey(value);
        if(entity!=null)
            getEntityManager().remove(entity);

    }
    
    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return getEntityManager().getCriteriaBuilder();
    }

    @Override
    public CriteriaQuery<TYPE> createCriteriaQuery() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        return cb.createQuery(getEntityClass());
    }
    
    @Override
    public int count() {

        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        Root<TYPE> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();

    }

    @Override
    public TYPE getByPrimaryKey(Integer value) {
        if (value == null) {
            throw new IllegalArgumentException(getClass().getName() + ".getByPrimaryKey(): Argument 'value' is null");
        }

        return getBy(getPrimaryKeyField().getName(), value);
    }
    
    @Override
    public TYPE get(CriteriaQuery<TYPE> cq) {
        return getEntity(cq) ;
    }

    @Override
    public TYPE getBy(String columnName, Object columnValue) {

        List<TYPE> v = (List<TYPE>) findBy(columnName, columnValue);
        if (v == null || v.isEmpty()) {
            return null;
        } else if (v.size() > 1) {
            return null;
        }

        return v.get(0);

    }

    @Override
    public List<TYPE> findAll() {
       CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();

        CriteriaQuery<TYPE> query = cb.createQuery(getEntityClass());
        Root<TYPE> r = query.from(getEntityClass());
        query.select(r);
        
        Field field = getPrimaryKeyField();
        query.orderBy(cb.asc(r.get(field.getName())));

        return findEntities(query);
    }


    @Override
    public List<TYPE> find(CriteriaQuery<TYPE> cq) {
        return findEntities(cq) ;
    }

    @Override
    public List<TYPE> findBy(String columnName, Object ... values) {
        
        if (columnName == null || columnName.length() == 0) {
            throw new IllegalArgumentException(getClass().getName() + ".findBy(): No columnName given");
        }

        if (values == null || values.length == 0) {
            throw new IllegalArgumentException(getClass().getName() + ".findBy(): No columnValue given");
        }

        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();

        CriteriaQuery<TYPE> query = cb.createQuery(getEntityClass());
        Root<TYPE> r = query.from(getEntityClass());
        query.select(r);

        List<Predicate> predicateList = new ArrayList<>();
        for (int i = 0; i < values.length; ++i) {
            if (values[i] != null) {
                predicateList.add(cb.equal(r.get(columnName), values[i]));
            } else {
                predicateList.add(cb.isNull(r.get(columnName)));
            }
        }

        query.where(predicateList.toArray(new Predicate[predicateList.size()]));

        return findEntities(query);
    }

    @Override
    public boolean isExist(Integer primaryKey) {

        if (primaryKey == null) {
            return false;
        }

        return getByPrimaryKey(primaryKey) != null;

    }

    @Override
    public boolean reload(TYPE entity) {
        try {
            getEntityManager().refresh(entity);
        } catch (EntityNotFoundException ex) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public void flush(){
        getEntityManager().flush();
    }

    ////////////////////////////////////////////////////////////////////////////    
    protected <TYPE> List<TYPE> findEntities(CriteriaQuery<TYPE> criteriaQuery) {
        
        TypedQuery<TYPE> typedQuery = getEntityManager().createQuery(criteriaQuery) ;
        return typedQuery.getResultList();
        
    }
    
    protected final <TYPE> List<TYPE> findEntities(Class<TYPE> klass, StringBuilder sbQlCmd, Object... args) {
        return findEntities(klass, sbQlCmd.toString(), args);
    }

    protected final <TYPE> List<TYPE> findEntities(Class<TYPE> klass, String qlCmd, Object... args) {

        if (klass == null) {
            throw new IllegalArgumentException(getClass().getName() + ".findEntities(): 參數 klass 不可為 null");
        }

        if (qlCmd == null) {
            throw new IllegalArgumentException(getClass().getName() + ".findEntities(): 參數 name 不可為 null");
        }

        javax.persistence.EntityManager em = getEntityManager();
        TypedQuery<TYPE> query = em.createQuery(qlCmd, klass);

        EmUtility.processQueryParameter(query, args);

        return query.getResultList();

    }

    protected final <TYPE> List<TYPE> findEntitiesByNativeQuery(Class<TYPE> klass, StringBuilder sbSqlCmd, Object... args) {
        return findEntitiesByNativeQuery(klass, sbSqlCmd.toString(), args);
    }

    protected final <TYPE> List<TYPE> findEntitiesByNativeQuery(Class<TYPE> klass, String sqlCmd, Object... args) {

        if (klass == null) {
            throw new IllegalArgumentException(getClass().getName() + ".findEntities(): 參數 klass 不可為 null");
        }

        if (sqlCmd == null) {
            throw new IllegalArgumentException(getClass().getName() + ".findEntities(): 參數 name 不可為 null");
        }

        javax.persistence.EntityManager em = getEntityManager();
        Query query = em.createNativeQuery(sqlCmd, klass);

        EmUtility.processQueryParameter(query, args);

        return query.getResultList();

    }

    protected final <TYPE> TYPE getEntity(CriteriaQuery<TYPE> criteriaQuery) {

        if (criteriaQuery == null) {
            throw new IllegalArgumentException(getClass().getName() + ".getEntity(): 參數 klass 不可為 null");
        }

        List<TYPE> list = findEntities(criteriaQuery);
        if (list == null || list.isEmpty()) {
            return null;
        } else if (list.size() > 1) {
            return null;
        }

        return list.iterator().next();
        
    }
    
    protected final <TYPE> TYPE getEntity(Class<TYPE> klass, StringBuilder sbQlCmd, Object... args) {
        return getEntity(klass, sbQlCmd.toString(), args);
    }

    protected final <TYPE> TYPE getEntity(Class<TYPE> klass, String qlCmd, Object... args) {

        if (klass == null) {
            throw new IllegalArgumentException(getClass().getName() + ".getEntity(): 參數 klass 不可為 null");
        }

        if (qlCmd == null) {
            throw new IllegalArgumentException(getClass().getName() + ".getEntity(): 參數 name 不可為 null");
        }

        List<TYPE> list = findEntities(klass, qlCmd, args);
        if (list == null || list.isEmpty()) {
            return null;
        } else if (list.size() > 1) {
            return null;
        }

        return list.iterator().next();

    }

    protected final <TYPE> TYPE getEntityByNativeQuery(Class<TYPE> klass, StringBuilder sbSqlCmd, Object... args) {
        return getEntityByNativeQuery(klass, sbSqlCmd.toString(), args);
    }

    protected final <TYPE> TYPE getEntityByNativeQuery(Class<TYPE> klass, String sqlCmd, Object... args) {

        if (klass == null) {
            throw new IllegalArgumentException(getClass().getName() + ".getEntity(): 參數 klass 不可為 null");
        }

        if (sqlCmd == null) {
            throw new IllegalArgumentException(getClass().getName() + ".getEntity(): 參數 name 不可為 null");
        }

        List<TYPE> list = findEntitiesByNativeQuery(klass, sqlCmd, args);
        if (list == null || list.isEmpty()) {
            return null;
        } else if (list.size() > 1) {
            return null;
        }

        return list.iterator().next();

    }
    
    protected List<TYPE> findEntities(String qlCmd, Object... args) {
        return findEntities(getEntityClass(), qlCmd, args);
    }

    protected List<TYPE> findEntities(StringBuilder sbQlCmd, Object... args) {
        return findEntities(getEntityClass(), sbQlCmd.toString(), args);
    }

    protected List<TYPE> findEntitiesByNativeQuery(String sqlCmd, Object... args) {
        return findEntitiesByNativeQuery(getEntityClass(), sqlCmd, args);
    }

    protected List<TYPE> findEntitiesByNativeQuery(StringBuilder sbSqlCmd, Object... args) {
        return findEntitiesByNativeQuery(getEntityClass(), sbSqlCmd.toString(), args);
    }

    protected TYPE getEntity(String qlCmd, Object... args) {
        return getEntity(getEntityClass(), qlCmd, args);
    }

    protected TYPE getEntity(StringBuilder sbQlCmd, Object... args) {
        return getEntity(getEntityClass(), sbQlCmd.toString(), args);
    }

    protected TYPE getEntityByNativeQuery(String sqlCmd, Object... args) {
        return getEntityByNativeQuery(getEntityClass(), sqlCmd, args);
    }

    protected TYPE getEntityByNativeQuery(StringBuilder sbSqlCmd, Object... args) {
        return getEntityByNativeQuery(getEntityClass(), sbSqlCmd.toString(), args);
    }

    // -------------------------------------------------------------------------
    protected int executeUpdate(String sqlCmd, Object... args) {

        if (sqlCmd == null) {
            throw new IllegalArgumentException(getClass().getName() + ".executeUpdate(): 參數 sqlCmd 不可為 null");
        } else if (sqlCmd.isEmpty()) {
            throw new IllegalArgumentException(getClass().getName() + ".executeUpdate(): 參數 sqlCmd 不可為空");
        }

        javax.persistence.EntityManager em = getEntityManager();
        Query query = em.createNativeQuery(sqlCmd);

        EmUtility.processQueryParameter(query, args);

        return query.executeUpdate();

    }

    protected int executeUpdate(StringBuilder sbSqlCmd, Object... args) {
        return executeUpdate(sbSqlCmd.toString(), args);
    }

    protected Field getPrimaryKeyField() {

        Field primaryKeyField = null;


        // ---------------------------------------------------------------------
        for (Class c = getEntityClass(); c != null && primaryKeyField == null; c = c.getSuperclass()) {
            Field[] fields = c.getDeclaredFields();

            for (Field field : fields) {
                if (field.isAnnotationPresent(Id.class)) {
                    primaryKeyField = field;
                    break;
                }
            }
        }

        if (primaryKeyField == null) {
            String pk_name = /*getEntityClass().getSimpleName()+*/  "Id";

            for (Class c = getEntityClass(); c != null && primaryKeyField == null; c = c.getSuperclass()) {
                Field[] fields = c.getDeclaredFields();
                for (Field field : fields) {
                    if (field.getName().equalsIgnoreCase(pk_name)) {
                        primaryKeyField = field;
                        break;
                    }
                }
            }
        }
        
        return primaryKeyField;

    }

    // --------------------------------------------------------- Logging Methods
    protected void logInfo(String message) {
        log(Level.INFO, message);
    }

    protected void logInfo(String message, Object param) {
        log(Level.INFO, message, param);
    }

    protected void logInfo(String message, Object[] params) {
        log(Level.INFO, message, params);
    }

    protected void logError(String message) {
        log(Level.SEVERE, message);
    }

    protected void logError(String message, Object param) {
        log(Level.SEVERE, message, param);
    }

    protected void logError(String message, Object[] params) {
        log(Level.SEVERE, message, params);
    }

    protected void logError(String message, Throwable throwable) {
        log(Level.SEVERE, message, throwable);
    }

    protected void logWarning(String message) {
        log(Level.WARNING, message);
    }

    protected void logWarning(String message, Object param) {
        log(Level.WARNING, message, param);
    }

    protected void logWarning(String message, Object[] params) {
        log(Level.WARNING, message, params);
    }

    protected void logWarning(String message, Throwable throwable) {
        log(Level.WARNING, message, throwable);
    }

    protected void log(Level logLevel, String message) {
        if (LOGGER.isLoggable(logLevel)) {
            LOGGER.log(logLevel, message);
        }
    }

    protected void log(Level logLevel, String message, Object param) {
        if (LOGGER.isLoggable(logLevel)) {
            LOGGER.log(logLevel, message, param);
        }
    }

    protected void log(Level logLevel, String message, Object[] params) {
        if (LOGGER.isLoggable(logLevel)) {
            LOGGER.log(logLevel, message, params);
        }
    }

    protected void log(Level logLevel, String message, Throwable throwable) {
        if (LOGGER.isLoggable(logLevel)) {
            LOGGER.log(logLevel, message, throwable);
        }
    }
}