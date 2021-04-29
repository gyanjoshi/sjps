package com.example.projectx.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


import com.example.projectx.dto.PublishedJournalDto;
import com.example.projectx.model.Journal;

public interface JournalRepository extends JpaRepository<Journal , Integer> {
	
	@Modifying(clearAutomatically = true)
	@Transactional
    @Query("UPDATE Journal SET coverImageFileName = :fileName WHERE Id = :jid")
	public void updateCoverPage(@Param("jid") int jid, @Param("fileName") String fileName);
	
	@Query("SELECT coverImageFileName FROM Journal WHERE Id = :jid")
	public String getCoverPageFileName(@Param("jid") int jid);
	
//	@Query("SELECT new com.example.projectx.dto.PublishedJournalDto(b.Id, a.Id, b.JournalTopic, \r\n" + 
//			"b.coverImageFileName, a.VolumeNum, a.IssueNum, a.year, a.month, \r\n" + 
//			"a.editorialFileName, a.journalFileName, a.uploaded_date, a.uploaded_by, a.status) \r\n" + 
//			"FROM JournalIssue a \r\n" + 
//			"LEFT JOIN Journal b \r\n" + 
//			"ON a.journal.Id = b.Id \r\n" + 
//			"WHERE a.status ='Published'"
//			+ "ORDER BY a.uploaded_date desc")
//	public List<PublishedJournalDto> getPublishedJournals();

}
