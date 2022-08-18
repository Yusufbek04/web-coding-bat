package org.example.condigbat.repository;

import org.example.condigbat.entity.Language;
import org.example.condigbat.payload.LanguageDTO;
import org.example.condigbat.projection.LanguageDTOProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LanguageRepository extends JpaRepository<Language,Integer> {

    boolean existsByTitle(String title);

    Optional<Language> getLanguageByTitle(String title);

    @Query(value = "SELECT * FROM get_string_result_of_query(:query)", nativeQuery = true)
    List<LanguageDTOProjection> getLanguagesByStringQuery(String query);


}
