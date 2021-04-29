/**
 * 
 */
package com.example.projectx.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.projectx.model.Person;

/**
 * @author Gyan Prakash Joshi
 * Mar 13, 2021
 * 
 *
 */
public interface PersonRepository extends JpaRepository<Person, Long> {
	
	

}
