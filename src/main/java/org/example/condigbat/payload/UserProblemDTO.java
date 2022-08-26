package org.example.condigbat.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.condigbat.entity.Problem;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserProblemDTO {

    private Integer userId;

    private Problem problem;

    private String solution;

    private Boolean solved;
}
