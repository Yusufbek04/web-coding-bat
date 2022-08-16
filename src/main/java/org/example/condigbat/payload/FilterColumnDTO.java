package org.example.condigbat.payload;

import lombok.Getter;
import org.example.condigbat.payload.enums.ConditionTypeEnum;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
public class FilterColumnDTO {

    private String title;

    @Enumerated(value = EnumType.STRING)
    private ConditionTypeEnum conditionType;

    private String value;

    private String from;

    private String till;
}
