package org.example.condigbat.payload;

import lombok.Getter;
import org.example.condigbat.payload.enums.OperatorTypeEnum;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.ArrayList;
import java.util.List;

@Getter
public class FilterDTO {

    @Enumerated(value = EnumType.STRING)
    private OperatorTypeEnum operatorType;//AND, OR

    private List<FilterColumnDTO> columns = new ArrayList<>();
}
