package org.example.condigbat.repository;

import org.example.condigbat.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LanguageRepository extends JpaRepository<Language,Integer> {

    boolean existsByTitle(String title);

    boolean existsByUrl(String url);

    Optional<Language> getLanguageByTitle(String title);

    Optional<Language> getLanguageByUrl(String title);

}
