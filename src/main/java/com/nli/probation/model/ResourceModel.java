package com.nli.probation.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Model for response pagination
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResourceModel<T> {
    private String searchText;
    private String sortType;
    private String sortBy;
    private int limit;
    private int index;
    private int totalResult;
    private int totalPage;
    private List<T> data;
}
