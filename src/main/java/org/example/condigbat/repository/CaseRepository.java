package org.example.condigbat.repository;

import org.example.condigbat.entity.Case;
import org.example.condigbat.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CaseRepository extends JpaRepository<Case,Long> {

    Optional<Case> getCaseByArgsAndProblemId(String args, Integer id);

}
