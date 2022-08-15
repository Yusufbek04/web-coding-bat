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
public class AddSectionDTO {

    @NotBlank(message = "title must be not blank")
    private String title;

    @NotBlank(message = "description must be not blank")
    private String description;

    @NotBlank(message = "maxRate must be not null")
    private Short maxRate;

    @NotBlank(message = "language must be not null")
    private LanguageDTO language;

}
