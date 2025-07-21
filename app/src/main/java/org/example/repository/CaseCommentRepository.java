package org.example.repository;

import java.util.List;
import java.util.Set;

import org.example.entity.CaseComment;
import org.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CaseCommentRepository extends JpaRepository<CaseComment, Long> {
    List<CaseComment> findByUser(User user);

    @Query("SELECT c FROM CaseComment c WHERE c.crimeCase.id IN :caseIds")
    List<CaseComment> findByCrimeCaseIdIn(@Param("caseIds") Set<Long> caseIds);
} 