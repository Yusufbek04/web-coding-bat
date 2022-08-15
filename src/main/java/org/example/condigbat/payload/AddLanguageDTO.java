package org.example.condigbat.payload;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AddLanguageDTO {

    @NotBlank(message = "Title must not be null")
    private String title;
}
