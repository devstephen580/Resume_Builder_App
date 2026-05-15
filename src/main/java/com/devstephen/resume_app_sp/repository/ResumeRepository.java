package com.devstephen.resume_app_sp.repository;

import com.devstephen.resume_app_sp.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, String> {
    List<Resume> findByUserIdOrderByUpdatedAtDesc(String userId);

    Optional<Resume> findByUserIdAndId(String id, String userId);
}
