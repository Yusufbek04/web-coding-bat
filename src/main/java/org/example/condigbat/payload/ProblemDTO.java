package org.example.condigbat.payload;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProblemDTO {

    private Integer id;

    private String title;

    private String description;

    private String methodSignature;

    private SectionDTO sectionDTO;

    private Long tryCount;

    private Long solutionCount;

    public ProblemDTO(String title, String description, String methodSignature, SectionDTO sectionDTO) {
        this.title = title;
        this.description = description;
        this.methodSignature = methodSignature;
        this.sectionDTO = sectionDTO;
    }

    public ProblemDTO(Integer id) {
        this.id = id;
    }
}
