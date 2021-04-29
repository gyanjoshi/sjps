package com.example.projectx.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.projectx.model.Journal;

public interface ArchiveRepository extends JpaRepository<Journal, Integer> {

}
