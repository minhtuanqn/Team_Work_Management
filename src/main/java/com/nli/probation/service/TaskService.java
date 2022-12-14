package com.nli.probation.service;

import com.nli.probation.constant.EntityStatusEnum;
import com.nli.probation.converter.PaginationConverter;
import com.nli.probation.customexception.NoSuchEntityException;
import com.nli.probation.entity.TaskEntity;
import com.nli.probation.entity.UserAccountEntity;
import com.nli.probation.metamodel.TaskEntity_;
import com.nli.probation.model.RequestPaginationModel;
import com.nli.probation.model.ResourceModel;
import com.nli.probation.model.task.CreateTaskModel;
import com.nli.probation.model.task.TaskModel;
import com.nli.probation.model.task.UpdateTaskModel;
import com.nli.probation.model.useraccount.UserAccountModel;
import com.nli.probation.repository.TaskRepository;
import com.nli.probation.repository.UserAccountRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserAccountRepository userAccountRepository;
    private final ModelMapper modelMapper;

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
        if(savedEntity.getUserAccountEntity() != null) {
            responseTaskModel.setAssignee(modelMapper.map(savedEntity.getUserAccountEntity(), UserAccountModel.class));
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
        if(deletedTaskEntity.getStatus() == EntityStatusEnum.TaskStatusEnum.DISABLE.ordinal())
            throw new NoSuchEntityException("This task was deleted");

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
        if(savedEntity.getUserAccountEntity() != null) {
            taskModel.setAssignee(modelMapper.map(savedEntity.getUserAccountEntity(), UserAccountModel.class));
        }
        return taskModel;
    }

    /**
     * Specification for search task by title
     * @param searchValue
     * @return specification
     */
    private Specification<TaskEntity> containsTitle(String searchValue) {
        return ((root, query, criteriaBuilder) -> {
            String pattern = searchValue != null ? "%" + searchValue + "%" : "%%";
            return criteriaBuilder.like(root.get(TaskEntity_.TITLE), pattern);
        });
    }

    /**
     * Specification for search task by assignee
     * @param userAccountEntity
     * @return specification
     */
    private Specification<TaskEntity> belongToAssignee(UserAccountEntity userAccountEntity) {
        return ((root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get(TaskEntity_.USER_ACCOUNT_ENTITY), userAccountEntity);
        });
    }

    /**
     * Search task like title
     * @param searchValue
     * @param paginationModel
     * @return resource of data
     */
    public ResourceModel<TaskModel> searchTasks(String searchValue, RequestPaginationModel paginationModel) {
        PaginationConverter<TaskModel, TaskEntity> paginationConverter = new PaginationConverter<>();

        //Build pageable
        String defaultSortBy = TaskEntity_.ID;
        Pageable pageable = paginationConverter.convertToPageable(paginationModel, defaultSortBy, TaskEntity.class);

        //Find all tasks
        Page<TaskEntity> taskEntityPage = taskRepository.findAll(containsTitle(searchValue), pageable);

        //Convert list of offices entity to list of task models
        List<TaskModel> taskModels = new ArrayList<>();
        for(TaskEntity entity : taskEntityPage) {
            TaskModel model = modelMapper.map(entity, TaskModel.class);
            if(entity.getUserAccountEntity() != null) {
                model.setAssignee(modelMapper.map(entity.getUserAccountEntity(), UserAccountModel.class));
            }
            taskModels.add(model);
        }

        //Prepare resource for return
        ResourceModel<TaskModel> resourceModel = new ResourceModel<>();
        resourceModel.setData(taskModels);
        resourceModel.setSearchText(searchValue);
        resourceModel.setSortBy(defaultSortBy);
        resourceModel.setSortType(paginationModel.getSortType());
        paginationConverter.buildPagination(paginationModel, taskEntityPage, resourceModel);
        return resourceModel;
    }

    /**
     * Assign/reassign a user for a task
     * @param taskId
     * @param userId
     * @return saved task model
     */
    public TaskModel assignTaskToUser(int taskId, int userId) {
        //Check task
        Optional<TaskEntity> taskOptional = taskRepository.findById(taskId);
        TaskEntity taskEntity = taskOptional.orElseThrow(() -> new NoSuchEntityException("Not found task"));

        //Check user
        Optional<UserAccountEntity> userOptional = userAccountRepository.findById(userId);
        UserAccountEntity userEntity = userOptional.orElseThrow(() -> new NoSuchEntityException("Not found user account"));

        //Update user in task
        taskEntity.setUserAccountEntity(userEntity);
        TaskEntity savedTaskEntity = taskRepository.save(taskEntity);

        //Prepare for response model
        TaskModel responseModel = modelMapper.map(savedTaskEntity, TaskModel.class);
        responseModel.setAssignee(modelMapper.map(savedTaskEntity.getUserAccountEntity(), UserAccountModel.class));
        return  responseModel;
    }

    /**
     * Search task of user id
     * @param searchValue
     * @param paginationModel
     * @param userId
     * @return resource that contains list of task
     */
    public ResourceModel<TaskModel> searchTasksOfUserId(String searchValue, RequestPaginationModel paginationModel, int userId) {
        //Check exist user account
        Optional<UserAccountEntity> accountOptional = userAccountRepository.findById(userId);
        UserAccountEntity accountEntity = accountOptional.orElseThrow(() -> new NoSuchEntityException("Not found user accpount"));

        PaginationConverter<TaskModel, TaskEntity> paginationConverter = new PaginationConverter<>();

        //Build pageable
        String defaultSortBy = TaskEntity_.ID;
        Pageable pageable = paginationConverter.convertToPageable(paginationModel, defaultSortBy, TaskEntity.class);

        //Find all tasks
        Page<TaskEntity> taskEntityPage = taskRepository.findAll(containsTitle(searchValue)
                .and(belongToAssignee(accountEntity)), pageable);

        //Convert list of offices entity to list of task models
        List<TaskModel> taskModels = new ArrayList<>();
        for(TaskEntity entity : taskEntityPage) {
            TaskModel model = modelMapper.map(entity, TaskModel.class);
            if(entity.getUserAccountEntity() != null) {
                model.setAssignee(modelMapper.map(entity.getUserAccountEntity(), UserAccountModel.class));
            }
            taskModels.add(model);
        }

        //Prepare resource for return
        ResourceModel<TaskModel> resourceModel = new ResourceModel<>();
        resourceModel.setData(taskModels);
        resourceModel.setSearchText(searchValue);
        resourceModel.setSortBy(defaultSortBy);
        resourceModel.setSortType(paginationModel.getSortType());
        paginationConverter.buildPagination(paginationModel, taskEntityPage, resourceModel);
        return resourceModel;
    }
}
