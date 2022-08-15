package org.example.condigbat.payload;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AddCaseDTO implements Serializable {

    @NotBlank(message = "args must be not blank")
    private String args;

    @NotBlank(message = "expected must be not blank")
    private String expected;

    @NotBlank(message = "visible must be not null")
    private Boolean visible;

    @NotBlank(message = "problem must be not null")
    private ProblemDTO problemDTO;

}
