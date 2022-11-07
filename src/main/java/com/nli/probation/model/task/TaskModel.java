package com.nli.probation.model.task;

import com.nli.probation.model.useraccount.UserAccountModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class TaskModel {
    private int id;

    private String title;

    private String description;

    private double estimatedTime;

    private UserAccountModel assignee;

    private double actualTime;

    private int status;
}
