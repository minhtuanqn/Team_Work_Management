package com.nli.probation.service;

import com.nli.probation.constant.EntityStatusEnum;
import com.nli.probation.customexception.DuplicatedEntityException;
import com.nli.probation.customexception.NoSuchEntityException;
import com.nli.probation.entity.TaskEntity;
import com.nli.probation.entity.TeamEntity;
import com.nli.probation.entity.UserAccountEntity;
import com.nli.probation.model.task.CreateTaskModel;
import com.nli.probation.model.task.TaskModel;
import com.nli.probation.model.task.UpdateTaskModel;
import com.nli.probation.model.team.CreateTeamModel;
import com.nli.probation.model.team.TeamModel;
import com.nli.probation.model.team.UpdateTeamModel;
import com.nli.probation.model.useraccount.UserAccountModel;
import com.nli.probation.repository.TaskRepository;
import com.nli.probation.repository.UserAccountRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.util.Optional;

@Service
public class TaskService {

    private TaskRepository taskRepository;
    private UserAccountRepository userAccountRepository;
    private ModelMapper modelMapper;

    public TaskService(TaskRepository taskRepository,
                       UserAccountRepository userAccountRepository,
                       ModelMapper modelMapper) {
        this.taskRepository = taskRepository;
        this.userAccountRepository = userAccountRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Create new task
     * @param createTaskModel
     * @return saved task
     */
    public TaskModel createTask(CreateTaskModel createTaskModel) {

        //Check assignee
        Optional<UserAccountEntity> existAccountOptional = userAccountRepository.findById(createTaskModel.getAssigneeId());
        UserAccountEntity existAccountEntity = existAccountOptional.orElse(null);

        //Prepare saved entity
        TaskEntity taskEntity = modelMapper.map(createTaskModel, TaskEntity.class);
        taskEntity.setStatus(EntityStatusEnum.TaskStatusEnum.ACTIVE.ordinal());
        taskEntity.setUserAccountEntity(existAccountEntity);
        taskEntity.setActualTime(0);

        //Save entity to DB
        TaskEntity savedEntity = taskRepository.save(taskEntity);
        TaskModel responseTaskModel = modelMapper.map(savedEntity, TaskModel.class);
        if(existAccountEntity != null) {
            responseTaskModel.setAssignee(modelMapper.map(existAccountEntity, UserAccountModel.class));
        }

        return responseTaskModel;
    }

    /**
     * Find task by id
     * @param id
     * @return found task
     */
    public TaskModel findTaskById(int id) {
        //Find task by id
        Optional<TaskEntity> searchedTaskOptional = taskRepository.findById(id);
        TaskEntity taskEntity = searchedTaskOptional.orElseThrow(() -> new NoSuchEntityException("Not found task"));
        TaskModel taskModel = modelMapper.map(taskEntity, TaskModel.class);
        if(taskEntity.getUserAccountEntity() != null) {
            taskModel.setAssignee(modelMapper.map(taskEntity.getUserAccountEntity(), UserAccountModel.class));
        }
        return taskModel;
    }

    /**
     * Delete a task
     * @param id
     * @return deleted model
     */
    public TaskModel deleteTaskById(int id) {
        //Find task by id
        Optional<TaskEntity> deletedTaskOptional = taskRepository.findById(id);
        TaskEntity deletedTaskEntity = deletedTaskOptional.orElseThrow(() -> new NoSuchEntityException("Not found task with id"));

        //Set status for entity
        deletedTaskEntity.setStatus(EntityStatusEnum.TaskStatusEnum.DISABLE.ordinal());

        //Save entity to DB
        TaskEntity responseEntity = taskRepository.save(deletedTaskEntity);
        TaskModel taskModel = modelMapper.map(responseEntity, TaskModel.class);
        if(taskModel.getAssignee() != null) {
            taskModel.setAssignee(modelMapper.map(taskModel.getAssignee(), UserAccountModel.class));
        }
        return taskModel;
    }

    /**
     * Update task information
     * @param updateTaskModel
     * @return updated task
     */
    public TaskModel updateTask (UpdateTaskModel updateTaskModel) {
        //Find task by id
        Optional<TaskEntity> foundTaskOptional = taskRepository.findById(updateTaskModel.getId());
        foundTaskOptional.orElseThrow(() -> new NoSuchEntityException("Not found task with id"));

        //Check assignee
        Optional<UserAccountEntity> existAccountOptional = userAccountRepository.findById(updateTaskModel.getAssigneeId());
        UserAccountEntity existAccountEntity = existAccountOptional.orElse(null);

        //Prepare saved entity
        TaskEntity taskEntity = modelMapper.map(updateTaskModel, TaskEntity.class);
        taskEntity.setUserAccountEntity(existAccountEntity);

        //Save entity to database
        TaskEntity savedEntity = taskRepository.save(taskEntity);
        TaskModel taskModel = modelMapper.map(savedEntity, TaskModel.class);
        if(taskEntity.getUserAccountEntity() != null) {
            taskModel.setAssignee(modelMapper.map(taskEntity.getUserAccountEntity(), UserAccountModel.class));
        }
        return taskModel;
    }
}
