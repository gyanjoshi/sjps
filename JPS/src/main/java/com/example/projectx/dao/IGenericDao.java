package com.example.projectx.dao;

import java.io.Serializable;
import java.util.List;

import com.example.projectx.model.JournalSection;

public interface IGenericDao<T extends Serializable> {

	   T findOne(Class<T> _clazz, final long id);

	   List<T> findAll(Class< T > _clazz);

	   void save(final T entity);

	   T update(final T entity);

	   void delete(final T entity);

	   void deleteById(Class<T> _clazz,final long entityId);

	}
