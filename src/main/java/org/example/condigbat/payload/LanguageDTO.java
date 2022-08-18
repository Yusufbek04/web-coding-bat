package org.example.condigbat.payload;

import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LanguageDTO {

    private Integer id;

    private String title;

    private String url;

    private Integer sectionCount;

    private Long tryCount;

    private Long solutionCount;

    public LanguageDTO(Integer id, String title, String url) {
        this.id = id;
        this.title = title;
        this.url = url;
    }

    public LanguageDTO(Integer id) {
        this.id = id;
    }

}
