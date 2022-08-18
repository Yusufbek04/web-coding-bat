package org.example.condigbat.payload;

import org.example.condigbat.payload.enums.SortingTypeEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;


public class SortLanguage implements Comparable<SortLanguage> {

    public static List<LanguageDTO> sort(List<LanguageDTO> languageDTOS, List<SortingDTO> sortingDTO) {

        TreeSet<SortLanguage> tree = new TreeSet<>(cast(languageDTOS, sortingDTO));

        return cast(tree.toArray(new SortLanguage[0]));
    }

    private static List<SortLanguage> cast(List<LanguageDTO> languageDTOS, List<SortingDTO> sortingDTO) {
        List<SortLanguage> res = new ArrayList<>();
        for (LanguageDTO languageDTO : languageDTOS)
            res.add(new SortLanguage(languageDTO, sortingDTO));
        return res;
    }

    private static List<LanguageDTO> cast(SortLanguage[] sortLanguages) {
        List<LanguageDTO> list = new ArrayList<>();
        for (SortLanguage item : sortLanguages)
            list.add(item.languageDTO);
        return list;
    }

    private final LanguageDTO languageDTO;

    private final List<SortingDTO> sortingDTOS;

    private SortLanguage(LanguageDTO languageDTO, List<SortingDTO> sortingDTO) {
        this.languageDTO = languageDTO;
        this.sortingDTOS = sortingDTO;
    }


    @Override
    public int compareTo(SortLanguage that) {
        if (sortingDTOS == null || sortingDTOS.size() == 0)
            return 1;

        int num;
        for (SortingDTO sortingDTO : sortingDTOS) {
            int asc = sortingDTO.getType() == SortingTypeEnum.ASC ? 1 : -1;
            switch (sortingDTO.getName()) {
                case "id"-> {
                    num = this.languageDTO.getId().compareTo(that.languageDTO.getId());
                    if (num!=0)
                        return asc*num;
                }
                case "title"-> {
                    num = this.languageDTO.getTitle().compareTo(that.languageDTO.getTitle());
                    if (num!=0)
                        return asc*num;
                }
                case "url"-> {
                    num = this.languageDTO.getUrl().compareTo(that.languageDTO.getUrl());
                    if (num!=0)
                        return asc*num;
                }
                case "sectionCount"-> {
                    num = this.languageDTO.getSectionCount().compareTo(that.languageDTO.getSectionCount());
                    if (num!=0)
                        return asc*num;
                }
                case "tryCount"-> {
                    num = this.languageDTO.getTryCount().compareTo(that.languageDTO.getTryCount());
                    if (num!=0)
                        return asc*num;
                }
                case "solutionCount"-> {
                    num = this.languageDTO.getSolutionCount().compareTo(that.languageDTO.getSolutionCount());
                    if (num!=0)
                        return asc*num;
                }
                default -> {
                    throw new IllegalArgumentException();
                }
            }

        }

        return 1;
    }

}