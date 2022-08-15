package org.example.condigbat.repository;

import org.example.condigbat.entity.Language;
import org.example.condigbat.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SectionRepository extends JpaRepository<Section,Integer> {

    boolean existsByTitle(String title);

    Integer countAllByLanguage_Id(Integer language_id);

    boolean existsByUrl(String url);

    Optional<Section> getSectionByTitle(String title);

    Optional<Section> getSectionByTitleAndLanguageId(String title, Integer id);

    Optional<Section> getSectionByUrl(String title);

    long countAllByLanguageId(Integer languageId);

}
