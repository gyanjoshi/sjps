package com.example.projectx.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.projectx.dto.UserDto;
import com.example.projectx.model.AppUser;


public interface UserRepository extends JpaRepository<AppUser, Long> {

	@Query("SELECT new com.example.projectx.dto.UserDto(userName, email, password) "
			+ " FROM AppUser A "
			+ " WHERE email = :email")
	List<UserDto> findByEmail(@Param("email") String email);
	
	List<AppUser> findByUserName(String uname);

	

//	@Query("SELECT * "
//			+ " FROM AppUser A "
//			+ " WHERE userName = :uname")
//	List<AppUser> findByUserName(@Param("uname") String uname);
//
//	@Query("SELECT * "
//			+ " FROM AppUser A "
//			+ " WHERE userName = :uname")
//	AppUser getOne(String userName);


}
