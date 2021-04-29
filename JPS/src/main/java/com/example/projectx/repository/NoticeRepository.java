package com.example.projectx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.projectx.model.Notice;



public interface NoticeRepository extends JpaRepository<Notice, Integer> {

	
	


	@Modifying(clearAutomatically = true)
	@Transactional
    @Query("UPDATE Notice SET NoticeNumber =:noticenumber,NoticeTitle=:noticetitle,noticeText= :noticetext  WHERE Id =:id")
	
	void updatenotice(@Param("id") int id, @Param("noticenumber") String noticenumber,@Param("noticetitle") String noticetitle,@Param("noticetext") String noticetext);

	


}
