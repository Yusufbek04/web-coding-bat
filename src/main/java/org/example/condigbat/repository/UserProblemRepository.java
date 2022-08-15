package org.example.condigbat.repository;

import org.example.condigbat.entity.UserProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserProblemRepository
        extends JpaRepository<UserProblem, Long> {

    long countAllByProblem_SectionLanguageId(Integer languageId);

    @Query("SELECT COUNT (up) FROM UserProblem up JOIN Problem p ON p = up.problem JOIN Section s ON s = p.section WHERE s.language.id = :languageId")
    long countAllByProblem_SectionLanguageIdJPQL(Integer languageId);

    long countAllBySolvedIsTrueAndProblem_SectionLanguageId(Integer problem_section_language_id);

    long countAllByProblem_Section_Id(Integer id);
    long countAllBySolvedAndProblem_Section_Id(Boolean bool,Integer id);

    Long countAllBySolvedAndProblem_Id(Boolean solved, Long problem_id);

    Long countAllByProblem_Id(Long id);



}
