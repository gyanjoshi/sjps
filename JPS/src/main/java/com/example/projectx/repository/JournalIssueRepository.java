package com.example.projectx.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.projectx.model.JournalIssue;

public interface JournalIssueRepository extends JpaRepository<JournalIssue , Integer> {

}
