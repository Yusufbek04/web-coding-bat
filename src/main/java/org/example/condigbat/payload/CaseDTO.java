package org.example.condigbat.payload;

import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CaseDTO implements Serializable {

    private Long id;

    private String args;

    private String expected;

    private Boolean visible;

    private ProblemDTO problemDTO;

    public CaseDTO( String args, String expected, Boolean visible, ProblemDTO problemDTO) {
        this.args = args;
        this.expected = expected;
        this.visible = visible;
        this.problemDTO = problemDTO;
    }

    public CaseDTO(Long id) {
        this.id = id;
    }
}
