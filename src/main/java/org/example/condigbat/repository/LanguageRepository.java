package org.example.condigbat.repository;

import org.example.condigbat.entity.Language;
import org.example.condigbat.payload.LanguageDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LanguageRepository extends JpaRepository<Language,Integer> {

    boolean existsByTitle(String title);

    boolean existsByUrl(String url);

    Optional<Language> getLanguageByTitle(String title);

    Optional<List<Language>> getLanguagesByTitleContains(String title);
    Optional<List<Language>> getLanguagesByTitleNotContains(String title);
    Optional<List<Language>> getLanguagesByUrlContains(String url);
    Optional<List<Language>> getLanguagesByUrlNotContains(String url);



}
