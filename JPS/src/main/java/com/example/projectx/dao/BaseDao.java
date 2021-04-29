/**
 * 
 */
package com.example.projectx.dao;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

/**
 * @author Gyan Prakash Joshi
 * Mar 18, 2021
 * 
 * This class is a generic base class for all Dao s.
 * @param <T>
 *
 */
@Transactional
public abstract class BaseDao<T extends Serializable> {
	
	
    @PersistenceContext(unitName = "entityManagerFactory")
    private EntityManager entityManager;

    public T findOne(Class<T> _clazz, final long id) {
        return entityManager.find(_clazz, id);
    }

    @SuppressWarnings("unchecked")
    public List<T> findAll(Class<T> _clazz) {
    	
    	String sql = " FROM "+_clazz.getName();
    	
    	Query query =  entityManager.createQuery(sql);
    	
    	
    	
        return query.getResultList();
    }

    public void save(final T entity) {
        entityManager.persist(entity);
    }

    public T update(final T entity) {
        return entityManager.merge(entity);
    }

    public void delete(final T entity) {
        entityManager.remove(entity);
    }

    public void deleteById(Class<T> _clazz, final long entityId) {
        final T entity = findOne(_clazz, entityId);
        delete(entity);
    }
    
    @SuppressWarnings("unchecked")
	public List<T> findAllByFieldCriteria(Class<T> _clazz, String field, Object value) throws Exception 
    {
    	long cnt = Arrays.asList(_clazz.getFields())
    		.stream()
    		.filter(f -> f.getName().equalsIgnoreCase(field))
    		.count();
    	
    	if(cnt == 0)
    	{
    		throw new Exception("Specified field is not defined for class "+_clazz.getName());
    	}
    	
    	if(cnt > 1)
    	{
    		throw new Exception("There are more than one fields with given field name "+field);
    	}
    	
    	
    			
    	
    	String sql = " FROM "+_clazz.getName()+" WHERE "+field+"= :val";
    	
    	Query query =  entityManager.createQuery(sql);
    	query.setParameter("val", value);
    	
    	
    	
        return query.getResultList();
    }

}
