package com.nli.probation.model.task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class CreateTaskModel {

    private String title;

    private String description;

    private double estimatedTime;

    private int assigneeId;

}
