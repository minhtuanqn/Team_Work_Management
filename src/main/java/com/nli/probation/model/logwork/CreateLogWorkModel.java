package com.nli.probation.model.logwork;

import com.nli.probation.model.task.TaskModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class CreateLogWorkModel {

    private int taskId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
