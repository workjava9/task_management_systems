package com.example.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.example.dto.comments.CommentDTO;
import com.example.dto.comments.CommentDTOToCommentConverter;
import com.example.dto.tasks.TaskDTO;
import com.example.dto.tasks.TaskDTOResponse;
import com.example.dto.tasks.TaskDTOToTaskConverter;
import com.example.dto.tasks.TaskToTaskDTOResponseConverter;
import com.example.exception.AuthorizationFailException;
import com.example.exception.ObjectNotFoundException;
import com.example.model.Comment;
import com.example.model.Task;
import com.example.model.TaskState;
import com.example.model.User;
import com.example.service.CommentService;
import com.example.service.TaskService;
import com.example.service.UserService;
import com.example.util.BindingResultValidation;
import com.example.validation.TaskValidation;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Tag(name = "Task manager API")
@RequestMapping(value = "/api/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @Getter
    private final UserService userService;

    private final CommentService commentService;

    private final TaskDTOToTaskConverter taskDTOToTaskConverter;

    private final TaskToTaskDTOResponseConverter taskToTaskDTOResponseConverter;

    private final CommentDTOToCommentConverter commentDTOToCommentConverter;

    private final TaskValidation taskValidation;


    @PostMapping(consumes = "application/json")
    @Operation(summary = "Create task", description = "Create new task")
    public ResponseEntity<HttpStatus> createTask(@RequestBody() @Valid TaskDTO taskDTO, BindingResult bindingResult) {
        taskValidation.validate(taskDTO, bindingResult);

        BindingResultValidation.bindingResultCheck(bindingResult);

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(taskDTOToTaskConverter);
        Task task = modelMapper.map(taskDTO, Task.class);

        taskService.createTask(task);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping(consumes = "application/json", produces = "application/json")
    @Operation(summary = "Update task data", description = "Updates and returns updated task data")
    public ResponseEntity<TaskDTO> updateTask(@RequestParam("id") @Parameter(name = "id", description = "updating task id", example = "1", required = true) int taskId, @RequestBody() @Valid TaskDTO newTaskDTO, BindingResult bindingResult) {
        taskValidation.validate(newTaskDTO, bindingResult);
        BindingResultValidation.bindingResultCheck(bindingResult);

        if (!ownerAuthorization(taskId)) throw new AuthorizationFailException("Not enough rights to update this task");

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(taskDTOToTaskConverter);
        Task newTask = modelMapper.map(newTaskDTO, Task.class);

        Task updatedTask = taskService.updateTask(taskId, newTask);
        ModelMapper modelMapperResult = new ModelMapper();
        modelMapperResult.addConverter(taskToTaskDTOResponseConverter);
        TaskDTO resultTask = modelMapper.map(updatedTask, TaskDTO.class);
        return ResponseEntity.ok(resultTask);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    @Operation(summary = "Get task by task id", description = "Returns task")
    public ResponseEntity<TaskDTOResponse> getTask(@PathVariable("id") @Parameter(name = "id", description = "task id", example = "1") int id) {

        Optional<Task> taskOptional = taskService.getTaskById(id);
        if (taskOptional.isEmpty()) throw new ObjectNotFoundException("Task not found");

        Task task = taskOptional.get();

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(taskToTaskDTOResponseConverter);
        TaskDTOResponse taskDTOResponse = modelMapper.map(task, TaskDTOResponse.class);

        return ResponseEntity.ok(taskDTOResponse);
    }

    @GetMapping(value = "/get-by-owner", produces = "application/json")
    @Operation(summary = "Get task by owner id", description = "Returns task list for owner")
    public ResponseEntity<List<TaskDTOResponse>> getTaskListByOwner(@RequestParam("id") @Parameter(name = "id", description = "Owner id", example = "1") int ownerId, @RequestParam(value = "page", required = false) @Parameter(name = "page", description = "Pagination. Page number. Start from 0", example = "2", required = false) Integer page, @RequestParam(value = "tasks_per_page", required = false) @Parameter(name = "tasks_per_page", description = "Pagination. Tasks per page", example = "2", required = false) Integer tasksPerPage) {

        List<Task> taskList;
        if (page == null || tasksPerPage == null) taskList = taskService.getTaskListByOwnerId(ownerId);
        else taskList = taskService.getTaskListByOwnerId(ownerId, page, tasksPerPage);


        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(taskToTaskDTOResponseConverter);
        List<TaskDTOResponse> taskDTOResponseList = taskList.stream().map(x -> modelMapper.map(x, TaskDTOResponse.class)).collect(Collectors.toList());

        return ResponseEntity.ok(taskDTOResponseList);
    }

    @GetMapping(value = "/get-by-executor", produces = "application/json")
    @Operation(summary = "Get task by executor id", description = "Returns task list for executor")
    public ResponseEntity<List<TaskDTO>> getTaskListByExecutor(@RequestParam("id") @Parameter(name = "id", description = "Executor id", example = "1") int executorId, @RequestParam(value = "page", required = false) @Parameter(name = "page", description = "Pagination. Page number. Start from 0", example = "2", required = false) Integer page, @RequestParam(value = "tasks_per_page", required = false) @Parameter(name = "tasks_per_page", description = "Pagination. Tasks per page", example = "2", required = false) Integer tasksPerPage) {

        List<Task> taskList;
        if (page == null || tasksPerPage == null) taskList = taskService.getTaskListByExecutorId(executorId);
        else taskList = taskService.getTaskListByExecutorId(executorId, page, tasksPerPage);

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(taskToTaskDTOResponseConverter);
        List<TaskDTO> taskDTOList = taskList.stream().map(x -> modelMapper.map(x, TaskDTO.class)).collect(Collectors.toList());


        return ResponseEntity.ok(taskDTOList);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task", description = "Delete task by id")
    public ResponseEntity<HttpStatus> deleteTask(@PathVariable("id") @Parameter(name = "id", description = "Deleting task id", example = "1") int taskId) {
        if (!ownerAuthorization(taskId)) throw new AuthorizationFailException("Not enough rights to delete this task");

        taskService.deleteTask(taskId);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/set-status")
    @Operation(summary = "Change task status", description = "Changing task status by task id")
    public ResponseEntity<HttpStatus> changeTaskStatus(@RequestParam("id") @Parameter(name = "id", description = "Task id", example = "1") int taskId, @RequestParam("task-state") TaskState taskState) {
        if (!ownerAuthorization(taskId))
            throw new AuthorizationFailException("Not enough rights to update state of this task");

        taskService.changeTaskStatus(taskId, taskState);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/set-executor")
    @Operation(summary = "Set executor", description = "Set executor id for task")
    public ResponseEntity<HttpStatus> setExecutor(@RequestParam("id") @Parameter(name = "id", description = "Task id", example = "1") int taskId, @RequestParam("executor-id") @Parameter(name = "executor-id", description = "Executor id for task", example = "1") int executorId) {
        if (!ownerAuthorization(taskId))
            throw new AuthorizationFailException("Not enough rights to set executor for this task");

        taskService.setExecutor(taskId, executorId);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping(value = "/add-comment", consumes = "application/json")
    @Operation(summary = "Create comment", description = "Create comment for task by task id")
    public ResponseEntity<HttpStatus> createComment(@RequestBody() @Valid CommentDTO commentDTO, BindingResult bindingResult) {
        BindingResultValidation.bindingResultCheck(bindingResult);

        Integer taskId = commentDTO.getTaskId();
        if (!(ownerAuthorization(taskId) || executorAuthorization(taskId)))
            throw new AuthorizationFailException("Not enough rights to add comment to this task");


        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(commentDTOToCommentConverter);
        Comment comment = modelMapper.map(commentDTO, Comment.class);

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        comment.setCommentOwner(user);

        commentService.createComment(comment);
        return ResponseEntity.ok(HttpStatus.OK);
    }


    private boolean ownerAuthorization(int taskId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Task> taskList = taskService.getTaskListByOwnerId(user.getId());

        for (Task task : taskList) {
            if (task.getId() == taskId) return true;
        }
        return false;
    }

    private boolean executorAuthorization(int taskId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Task> taskList = taskService.getTaskListByExecutorId(user.getId());

        for (Task task : taskList) {
            if (task.getId() == taskId) return true;
        }
        return false;
    }

}
