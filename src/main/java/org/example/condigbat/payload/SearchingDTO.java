package org.example.condigbat.payload;

import lombok.Getter;

import java.util.List;

@Getter
public class SearchingDTO {

    private List<String> columns;

    private String value;
}
