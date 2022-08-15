package org.example.condigbat.repository;

import org.example.condigbat.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProblemRepository extends JpaRepository<Problem, Integer> {

    Optional<Problem> getProblemByTitleAndSectionId(String title, Integer section_id);

    long countProblemBySectionId(Integer id);

}
