package com.nli.probation.controller;

import com.nli.probation.converter.PaginationConverter;
import com.nli.probation.entity.TaskEntity;
import com.nli.probation.entity.TeamEntity;
import com.nli.probation.metamodel.TeamEntity_;
import com.nli.probation.model.RequestPaginationModel;
import com.nli.probation.model.ResourceModel;
import com.nli.probation.model.ResponseModel;
import com.nli.probation.model.task.CreateTaskModel;
import com.nli.probation.model.task.TaskModel;
import com.nli.probation.model.task.UpdateTaskModel;
import com.nli.probation.model.team.TeamModel;
import com.nli.probation.resolver.annotation.RequestPagingParam;
import com.nli.probation.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@Validated
@RequestMapping("/tasks")
public class TaskController {
    private TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Create new task
     * @param createModel
     * @return response entity contains created model
     */
    @PostMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseModel> createTask(@Valid @RequestBody CreateTaskModel createModel) {
        TaskModel savedModel = taskService.createTask(createModel);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(savedModel)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Find task by id
     * @param id
     * @return response entity contains model
     */
    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseModel> findTaskById(@PathVariable int id) {
        TaskModel foundTask = taskService.findTaskById(id);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(foundTask)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Delete a task by id
     * @param id
     * @return response entity contains deleted model
     */
    @DeleteMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseModel> deleteTask(@PathVariable int id) {
        TaskModel deletedModel = taskService.deleteTaskById(id);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(deletedModel)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);

    }

    /**
     * Update task
     * @param requestModel
     * @return response entity contains model
     */
    @PutMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseModel> updateTask(@Valid @RequestBody UpdateTaskModel requestModel) {
        TaskModel updatedModel = taskService.updateTask(requestModel);
        ResponseModel responseModel = new ResponseModel().statusCode(HttpStatus.OK.value())
                .data(updatedModel)
                .message("OK");
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }

    /**
     * Search tasks
     * @param requestPaginationModel
     * @param searchText
     * @return response entity contains data resource
     */
    @GetMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> searchTasks(@RequestPagingParam RequestPaginationModel requestPaginationModel,
                                              @RequestParam(value = "searchText", defaultValue = "") String searchText) {
        ResourceModel<TaskModel> taskList = taskService.searchTasks(searchText, requestPaginationModel);
        return new ResponseEntity<>(taskList, HttpStatus.OK);
    }
}
