package org.example.condigbat.payload;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddProblemDTO {

    @NotBlank(message = "title must be not blank")
    private String title;

    @NotBlank(message = "description must be not blank")
    private String description;

    @NotBlank(message = "methodSignature must be not blank")
    private String methodSignature;

    @NotBlank(message = "sectionDTO must be not blank")
    private SectionDTO sectionDTO;



}
