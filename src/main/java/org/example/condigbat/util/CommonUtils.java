package org.example.condigbat.util;

import org.example.condigbat.entity.Language;
import org.example.condigbat.payload.LanguageDTO;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CommonUtils {

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("\\s");
    private static final Pattern EDGES_DASHES = Pattern.compile("(^-|-$)");

    public static String makeUrl(String input) {
        String nonWhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nonWhitespace, Normalizer.Form.NFD);
        String slug = NON_LATIN.matcher(normalized).replaceAll("");
        slug = EDGES_DASHES.matcher(slug).replaceAll("");
        return slug.toLowerCase();
    }


    public static List<LanguageDTO> languageToLanguageDTO(List<Language> list) {
        List<LanguageDTO> res = new ArrayList<>();
        for (Language language : list) {
            res.add(new LanguageDTO(
                    language.getId(),
                    language.getTitle(),
                    CommonUtils.makeUrl(language.getUrl())
            ));
        }
        return res;
    }
    public static LanguageDTO languageToLanguageDTO(Language language) {
        return new LanguageDTO(
                language.getId(),
                language.getTitle(),
                language.getUrl()
        );
    }

    public static List<Language> languagesDTOToLanguages(List<LanguageDTO> list) {
        List<Language> res = new ArrayList<>();
        for (LanguageDTO languageDTO : list) {
            Language language = new Language();
            language.setId(languageDTO.getId());
            language.setTitle(languageDTO.getTitle());
            language.setUrl(languageDTO.getUrl());
            res.add(language);
        }
        return res;
    }
    public static Language languageDTOToLanguage(LanguageDTO languageDTO) {
        Language language = new Language();
        language.setId(languageDTO.getId());
        language.setTitle(languageDTO.getTitle());
        language.setUrl(languageDTO.getUrl());
        return language;
    }
}
