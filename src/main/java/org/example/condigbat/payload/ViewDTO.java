package org.example.condigbat.payload;

import lombok.Getter;
import org.apache.catalina.LifecycleState;

import java.util.List;

@Getter
public class ViewDTO {

    private SearchingDTO searching;

    private List<SortingDTO> sorting;

    private FilterDTO filtering;
}
