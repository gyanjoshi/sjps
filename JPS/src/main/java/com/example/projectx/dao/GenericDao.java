/**
 * 
 */
package com.example.projectx.dao;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import java.io.Serializable;

/**
 * @author Gyan Prakash Joshi
 * Mar 18, 2021
 *
 */
@Repository
@Scope( BeanDefinition.SCOPE_PROTOTYPE )

public class GenericDao<T extends Serializable> extends BaseDao<T> implements IGenericDao<T> {



}
