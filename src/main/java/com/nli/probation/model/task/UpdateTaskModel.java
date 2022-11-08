package com.nli.probation.model.task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class UpdateTaskModel {
    private int id;

    private String title;

    private String description;

    private LocalDateTime startTime;

    private double estimatedTime;

    private int assigneeId;

    private double actualTime;

    private int status;
}
