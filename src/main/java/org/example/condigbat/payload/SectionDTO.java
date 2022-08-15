package org.example.condigbat.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.example.condigbat.entity.Language;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SectionDTO {

    private Integer id;

    private String title;

    private String url;


    private String description;

    private Short maxRate;

    private LanguageDTO language;

    private Long problemCount;

    private Long tryCount;

    private Long solutionCount;

    public SectionDTO(Integer id, String title, String url, String description, Short maxRate, LanguageDTO language) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.description = description;
        this.maxRate = maxRate;
        this.language = language;
    }


    public SectionDTO(Integer id) {
        this.id = id;
    }
}
