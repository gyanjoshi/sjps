package com.example.projectx.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.projectx.dto.EditorDto;
import com.example.projectx.model.Editor;

public interface EditorRepository extends JpaRepository<Editor,Long> {

	
	
	@Query("SELECT new com.example.projectx.dto.EditorDto(email,phone, fullName, active) "
			+ " FROM Editor A "
			+ " WHERE email = :email")
	List<EditorDto> findEditorByEmail(@Param("email") String email);

}
