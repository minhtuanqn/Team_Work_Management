package com.nli.probation.service;

import com.nli.probation.constant.EntityStatusEnum;
import com.nli.probation.customexception.NoSuchEntityException;
import com.nli.probation.customexception.TimeCustomException;
import com.nli.probation.entity.LogWorkEntity;
import com.nli.probation.entity.TaskEntity;
import com.nli.probation.model.logwork.CreateLogWorkModel;
import com.nli.probation.model.logwork.LogWorkModel;
import com.nli.probation.model.logwork.UpdateLogWorkModel;
import com.nli.probation.model.task.TaskModel;
import com.nli.probation.repository.LogWorkRepository;
import com.nli.probation.repository.TaskRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Duration;
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
        double totalTime = existedTaskEntity.getActualTime()
                + Duration.between(logWorkEntity.getStartTime(), logWorkEntity.getEndTime()).toMinutes() / 60.0;
        existedTaskEntity.setActualTime(totalTime);
        logWorkEntity.setTaskEntity(existedTaskEntity);

        //Save entity to DB
        LogWorkEntity savedEntity = logWorkRepository.save(logWorkEntity);
        LogWorkModel responseLogModel = modelMapper.map(savedEntity, LogWorkModel.class);
        responseLogModel.setTaskModel(modelMapper.map(existedTaskEntity, TaskModel.class));

        return responseLogModel;
    }

    /**
     * Find log work by id
     * @param id
     * @return found log work
     */
    public LogWorkModel findLogWorkById(int id) {
        //Find log work by id
        Optional<LogWorkEntity> searchedLogOptional = logWorkRepository.findById(id);
        LogWorkEntity logWorkEntity = searchedLogOptional.orElseThrow(() -> new NoSuchEntityException("Not found log work"));
        LogWorkModel logWorkModel = modelMapper.map(logWorkEntity, LogWorkModel.class);
        logWorkModel.setTaskModel(modelMapper.map(logWorkEntity.getTaskEntity(), TaskModel.class));
        return logWorkModel;
    }

    /**
     * Delete a log work
     * @param id
     * @return deleted model
     */
    public LogWorkModel deleteLogWorkById(int id) {
        //Find log work by id
        Optional<LogWorkEntity> deletedLogWorkOptional = logWorkRepository.findById(id);
        LogWorkEntity deletedLogEntity = deletedLogWorkOptional.orElseThrow(() -> new NoSuchEntityException("Not found log with id"));

        //Set status for entity
        deletedLogEntity.setStatus(EntityStatusEnum.LogWorkStatusEnum.DISABLE.ordinal());

        //Save entity to DB
        LogWorkEntity responseEntity = logWorkRepository.save(deletedLogEntity);
        LogWorkModel logWorkModel = modelMapper.map(responseEntity, LogWorkModel.class);
        logWorkModel.setTaskModel(modelMapper.map(responseEntity.getTaskEntity(), TaskModel.class));
        return logWorkModel;
    }

    /**
     * Update log work information
     * @param updateLogWorkModel
     * @return updated log work
     */
    public LogWorkModel updateLogWork (UpdateLogWorkModel updateLogWorkModel) {
        //Find log work by id
        Optional<LogWorkEntity> foundLogOptional = logWorkRepository.findById(updateLogWorkModel.getId());
        LogWorkEntity foundLogEntity = foundLogOptional
                .orElseThrow(() -> new NoSuchEntityException("Not found log work with id"));

        //Check task
        Optional<TaskEntity> existTaskOptional = taskRepository.findById(updateLogWorkModel.getTaskId());
        TaskEntity existTaskEntity = existTaskOptional.orElseThrow(() -> new NoSuchEntityException("Not found task"));

        //Check time
        if(updateLogWorkModel.getStartTime().isAfter(updateLogWorkModel.getEndTime()))
            throw new TimeCustomException("Check time of log work again");

        //Prepare saved entity
        LogWorkEntity logWorkEntity = modelMapper.map(updateLogWorkModel, LogWorkEntity.class);
        double newTimeOfLog = Duration.between(logWorkEntity.getStartTime(), logWorkEntity.getEndTime()).toMinutes() / 60.0;
        double oldTimeOfLog = Duration.between(logWorkEntity.getStartTime(), logWorkEntity.getEndTime()).toMinutes() / 60.0;
        double newActualTimeOfTask = existTaskEntity.getActualTime() - oldTimeOfLog + newTimeOfLog;
        existTaskEntity.setActualTime(newActualTimeOfTask);
        logWorkEntity.setTaskEntity(existTaskEntity);

        //Save entity to database
        LogWorkEntity savedEntity = logWorkRepository.save(logWorkEntity);
        LogWorkModel logWorkModel = modelMapper.map(savedEntity, LogWorkModel.class);
        logWorkModel.setTaskModel(modelMapper.map(existTaskEntity, TaskModel.class));
        return logWorkModel;
    }
}
