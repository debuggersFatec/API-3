package com.api_3.api_3.service.task;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_3.api_3.dto.request.CreateTaskRequest;
import com.api_3.api_3.exception.InvalidResponsibleException;
import com.api_3.api_3.exception.ProjectNotFoundException;
import com.api_3.api_3.exception.TeamNotFoundException;
import com.api_3.api_3.model.entity.Projects;
import com.api_3.api_3.model.entity.Task;
import com.api_3.api_3.model.entity.Teams;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.repository.ProjectsRepository;
import com.api_3.api_3.repository.TaskRepository;
import com.api_3.api_3.repository.TeamsRepository;
import com.api_3.api_3.repository.UserRepository;
import com.api_3.api_3.service.GoogleCalendarService;
import com.api_3.api_3.service.NotificationService;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

@Service
public class CreateTaskService {
    @Autowired private TaskRepository taskRepository;
    @Autowired private ProjectsRepository projectsRepository;
    @Autowired private TeamsRepository teamsRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private NotificationService notificationService;
    @Autowired private GoogleCalendarService googleCalendarService;

    @Transactional
    public Task execute(CreateTaskRequest request) {
        Projects project = projectsRepository.findById(request.getProject_uuid())
            .orElseThrow(() -> new ProjectNotFoundException("Projeto com ID " + request.getProject_uuid() + " não encontrado."));

        Teams team = teamsRepository.findById(project.getTeamUuid())
            .orElseThrow(() -> new TeamNotFoundException("Team com ID " + project.getTeamUuid() + " não encontrado."));

        Task newTask = new Task();
        newTask.setUuid(UUID.randomUUID().toString());
        newTask.setTitle(request.getTitle());
        newTask.setDescription(request.getDescription());
        newTask.setDue_date(request.getDue_date());
        newTask.setStatus(request.getStatus() != null ? Task.Status.valueOf(request.getStatus().toUpperCase().replace('-', '_')) : Task.Status.NOT_STARTED);
        newTask.setPriority(request.getPriority() != null ? Task.Priority.valueOf(request.getPriority().toUpperCase()) : Task.Priority.LOW);
        newTask.setEquip_uuid(project.getTeamUuid());
        newTask.setProjectUuid(request.getProject_uuid());
        newTask.setRequiredFile(request.getRequiredFile());
        newTask.setIsRequiredFile(request.getIsRequiredFile());
        
        if (request.getResponsible() != null) {
            var r = request.getResponsible();
            newTask.setResponsible(new User.UserRef(r.getUuid(), r.getName(), r.getUrl_img()));
        } else {
            newTask.setResponsible(null);
        }

        validateResponsible(newTask, team);
        Task savedTask = taskRepository.save(newTask);

        if (project.getTasks() == null) project.setTasks(new java.util.ArrayList<>());
        project.getTasks().add(savedTask.toProjectRef());
        projectsRepository.save(project);

        if (savedTask.getResponsible() != null && savedTask.getResponsible().uuid() != null) {
            addTaskToUser(savedTask, savedTask.getResponsible().uuid());
        }

        notificationService.notifyTaskCreated(savedTask);

        return savedTask;
    }

    private void validateResponsible(Task task, Teams team) {
    if (task.getResponsible() != null && task.getResponsible().uuid() != null) {
            String responsibleUuid = task.getResponsible().uuid();
            userRepository.findById(responsibleUuid)
                    .orElseThrow(() -> new InvalidResponsibleException("Usuário responsável com ID " + responsibleUuid + " não encontrado."));

            if (task.getProjectUuid() == null) {
                throw new InvalidResponsibleException("Tarefa não está vinculada a um projeto válido.");
            }
            com.api_3.api_3.model.entity.Projects project = projectsRepository.findById(task.getProjectUuid())
                .orElseThrow(() -> new ProjectNotFoundException("Projeto com ID " + task.getProjectUuid() + " não encontrado."));

            boolean isProjectMember = project.getMembers() != null && project.getMembers().stream()
                .filter(java.util.Objects::nonNull)
                .anyMatch(m -> responsibleUuid.equals(m.getUuid()));
            if (!isProjectMember) {
                throw new InvalidResponsibleException("O usuário responsável não é membro do projeto.");
            }

            boolean isTeamMember = team.getMembers() != null && team.getMembers().stream()
                .filter(java.util.Objects::nonNull)
                .anyMatch(m -> responsibleUuid.equals(m.getUuid()));
            if (!isTeamMember) {
                throw new InvalidResponsibleException("O usuário responsável não é membro da equipe.");
            }
        }
    }

    private void addTaskToUser(Task task, String userUuid) {
        userRepository.findById(userUuid).ifPresent(user -> {
            if (user.getTasks() == null) user.setTasks(new ArrayList<>());
            Task.TaskUser taskUser = task.toUserRef();
            user.getTasks().add(taskUser);
            userRepository.save(user);
        });
        if (task.getDue_date() != null) {
            addTaskToGoogleCalendar(task, userRepository.findById(userUuid).orElse(null));
        }
    }

    private void addTaskToGoogleCalendar(Task task, User user) {
        if (user.getGoogleCalendar() == null || !user.getGoogleCalendar().isConnected()) {
            return;
        }

        try {
            Event event = new Event()
                .setSummary(task.getTitle())
                .setDescription(task.getDescription());

            LocalDate localDate = task.getDue_date().toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate();

            event.setStart(new EventDateTime().setDate(new DateTime(localDate.toString())));
            event.setEnd(new EventDateTime().setDate(new DateTime(localDate.plusDays(1).toString())));

            Event createdEvent = googleCalendarService.criarEvento(user.getUuid(), event);
            if (createdEvent != null) {
                task.setGoogleEventId(createdEvent.getId());
                taskRepository.save(task);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
