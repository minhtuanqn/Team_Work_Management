package com.nli.probation.service;

import com.nli.probation.constant.EntityStatusEnum;
import com.nli.probation.customexception.NoSuchEntityException;
import com.nli.probation.customexception.TimeCustomException;
import com.nli.probation.entity.LogWorkEntity;
import com.nli.probation.entity.TaskEntity;
import com.nli.probation.model.logwork.CreateLogWorkModel;
import com.nli.probation.model.logwork.LogWorkModel;
import com.nli.probation.model.task.TaskModel;
import com.nli.probation.repository.LogWorkRepository;
import com.nli.probation.repository.TaskRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LogWorkService {
    private LogWorkRepository logWorkRepository;
    private ModelMapper modelMapper;
    private TaskRepository taskRepository;

    public LogWorkService(LogWorkRepository logWorkRepository,
                          ModelMapper modelMapper,
                          TaskRepository taskRepository) {
        this.logWorkRepository = logWorkRepository;
        this.modelMapper = modelMapper;
        this.taskRepository = taskRepository;
    }

    /**
     * Create new log work
     * @param createLogWorkModel
     * @return saved log work
     */
    public LogWorkModel createLogWork(CreateLogWorkModel createLogWorkModel) {
        //Check exist task
        Optional<TaskEntity> existedTaskOptional = taskRepository.findById(createLogWorkModel.getTaskId());
        TaskEntity existedTaskEntity = existedTaskOptional
                .orElseThrow(() -> new NoSuchEntityException("Not found task"));

        //Check time
        if(createLogWorkModel.getStartTime().isAfter(createLogWorkModel.getEndTime()))
            throw new TimeCustomException("Check time of log work again");

        //Prepare saved entity
        LogWorkEntity logWorkEntity = modelMapper.map(createLogWorkModel, LogWorkEntity.class);
        logWorkEntity.setStatus(EntityStatusEnum.LogWorkStatusEnum.ACTIVE.ordinal());
        logWorkEntity.setTaskEntity(existedTaskEntity);

        //Save entity to DB
        LogWorkEntity savedEntity = logWorkRepository.save(logWorkEntity);
        LogWorkModel responseLogModel = modelMapper.map(savedEntity, LogWorkModel.class);
        responseLogModel.setTaskModel(modelMapper.map(existedTaskEntity, TaskModel.class));

        return responseLogModel;
    }
}
