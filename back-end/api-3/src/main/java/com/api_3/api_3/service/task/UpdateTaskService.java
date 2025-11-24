package com.api_3.api_3.service.task;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_3.api_3.dto.request.UpdateTaskRequest;
import com.api_3.api_3.exception.InvalidResponsibleException;
import com.api_3.api_3.exception.ProjectNotFoundException;
import com.api_3.api_3.exception.TaskNotFoundException;
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
public class UpdateTaskService {

    @Autowired private TaskRepository taskRepository;
    @Autowired private TeamsRepository teamsRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ProjectsRepository projectsRepository;
    @Autowired private NotificationService notificationService;
    @Autowired private GoogleCalendarService googleCalendarService;

    @Transactional
    public Task execute(String uuid, UpdateTaskRequest request) {
        Task existingTask = taskRepository.findById(uuid)
                .orElseThrow(() -> new TaskNotFoundException("Tarefa não encontrada para atualizar com o ID: " + uuid));

        String oldResponsibleUuid = (existingTask.getResponsible() != null) ? existingTask.getResponsible().uuid() : null;
        String newResponsibleUuid = (request.getResponsible() != null) ? request.getResponsible().getUuid() : null;

    String oldTitle = existingTask.getTitle();
    java.util.Date oldDue = existingTask.getDue_date();

        existingTask.setTitle(request.getTitle());
        existingTask.setDescription(request.getDescription());
        existingTask.setDue_date(request.getDue_date());
        existingTask.setStatus(request.getStatus() != null ? Task.Status.valueOf(request.getStatus().toUpperCase().replace('-', '_')) : existingTask.getStatus());
        existingTask.setPriority(request.getPriority() != null ? Task.Priority.valueOf(request.getPriority().toUpperCase()) : existingTask.getPriority());
        if (request.getIsRequiredFile() != null) {
            existingTask.setIsRequiredFile(request.getIsRequiredFile());
        }
        if (request.getResponsible() != null) {
            var r = request.getResponsible();
            User.UserRef newResponsibleRef = new User.UserRef(r.getUuid(), r.getName(), r.getUrl_img());
            existingTask.setResponsible(newResponsibleRef);
        } else {
            existingTask.setResponsible(null);
        }

        if (!Objects.equals(oldResponsibleUuid, newResponsibleUuid)) {
             validateResponsible(existingTask);
        }
    Task savedTask = taskRepository.save(existingTask);

        if (savedTask.getProjectUuid() != null) {
            projectsRepository.findById(savedTask.getProjectUuid()).ifPresent(project -> {
                boolean changed = false;
                if (project.getTasks() != null) {
                    Optional<Task.TaskProject> oldRefOpt = project.getTasks().stream()
                        .filter(tp -> tp != null && tp.uuid().equals(savedTask.getUuid()))
                        .findFirst();

                    if (oldRefOpt.isPresent()) {
                         Task.TaskProject newRef = savedTask.toProjectRef();
                         if (!oldRefOpt.get().equals(newRef)) {
                             project.getTasks().remove(oldRefOpt.get());
                             project.getTasks().add(newRef);
                             changed = true;
                         }
                    } else {
                        project.getTasks().add(savedTask.toProjectRef());
                        changed = true;
                    }
                } else {
                     project.setTasks(new ArrayList<>());
                     project.getTasks().add(savedTask.toProjectRef());
                     changed = true;
                }

                if (changed) {
                    projectsRepository.save(project);
                }
            });
        }


        manageUserTaskAssignment(savedTask, oldResponsibleUuid, newResponsibleUuid);

        if (!Objects.equals(oldResponsibleUuid, newResponsibleUuid)) {
            if (oldResponsibleUuid == null && newResponsibleUuid != null) {
                notificationService.notifyTaskUpdatedBroadcast(savedTask);
            } else if (oldResponsibleUuid != null && newResponsibleUuid == null) {
                notificationService.notifyTaskUnassigned(savedTask, oldResponsibleUuid);
                notificationService.notifyTaskUpdatedScoped(savedTask);
            } else {
                notificationService.notifyTaskUnassigned(savedTask, oldResponsibleUuid);
                notificationService.notifyTaskAssigned(savedTask, newResponsibleUuid);
            }
        }

    boolean changedTitle = request.getTitle() != null && !request.getTitle().equals(oldTitle);
    boolean changedDue = request.getDue_date() != null && (oldDue == null || !request.getDue_date().equals(oldDue));
        if (changedTitle || changedDue) {
            notificationService.notifyTaskUpdatedScoped(savedTask);
        }

        try {
            if (savedTask.getDueDate() == null && oldDue != null) {
                googleCalendarService.excluirEvento(oldResponsibleUuid, savedTask.getGoogleEventId());
                savedTask.setGoogleEventId(null);
                taskRepository.save(savedTask);
            }
            if (savedTask.getDueDate() != null) {
                Event event = new Event()
                    .setSummary(savedTask.getTitle())
                    .setDescription(savedTask.getDescription());

                LocalDate localDate = savedTask.getDue_date().toInstant()
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate();

                event.setStart(new EventDateTime().setDate(new DateTime(localDate.toString())));
                event.setEnd(new EventDateTime().setDate(new DateTime(localDate.plusDays(1).toString())));
                Event createdEvent = null;
                if (savedTask.getGoogleEventId() == null) {
                    if (newResponsibleUuid != null) {
                        createdEvent = googleCalendarService.criarEvento(newResponsibleUuid, event);
                    }
                } else if (oldResponsibleUuid != null && newResponsibleUuid != null) {
                    if (oldResponsibleUuid.equals(newResponsibleUuid)) {
                        if (oldDue != null) {
                            googleCalendarService.atualizarEvento(oldResponsibleUuid, savedTask.getGoogleEventId(), event);
                        }
                    } else {
                        createdEvent = googleCalendarService.criarEvento(newResponsibleUuid, event);
                        if (oldDue != null) {
                            googleCalendarService.excluirEvento(oldResponsibleUuid, savedTask.getGoogleEventId());
                        }
                    }
                }
                else if (oldResponsibleUuid != null && newResponsibleUuid == null) {
                    if (oldDue == null) {
                        googleCalendarService.excluirEvento(oldResponsibleUuid, savedTask.getGoogleEventId());
                    }
                }
                else if (oldResponsibleUuid == null && newResponsibleUuid != null) {
                    createdEvent = googleCalendarService.criarEvento(newResponsibleUuid, event);
                }
                if (createdEvent != null) {
                    savedTask.setGoogleEventId(createdEvent.getId());
                    taskRepository.save(savedTask);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return savedTask;
    }

    private void validateResponsible(Task task) {
        Teams team = teamsRepository.findById(task.getEquip_uuid())
                .orElseThrow(() -> new TeamNotFoundException("Team com ID " + task.getEquip_uuid() + " não encontrado para a tarefa " + task.getUuid()));

        if (task.getResponsible() != null && task.getResponsible().uuid() != null) {
            String responsibleUuid = task.getResponsible().uuid();
            userRepository.findById(responsibleUuid)
                    .orElseThrow(() -> new InvalidResponsibleException("Usuário responsável com ID " + responsibleUuid + " não encontrado."));

            if (task.getProjectUuid() == null) {
                throw new InvalidResponsibleException("Tarefa não está vinculada a um projeto válido.");
            }
            Projects project = projectsRepository.findById(task.getProjectUuid())
                .orElseThrow(() -> new ProjectNotFoundException("Projeto com ID " + task.getProjectUuid() + " não encontrado."));

            boolean isProjectMember = project.getMembers() != null && project.getMembers().stream()
                .anyMatch(m -> m != null && responsibleUuid.equals(m.getUuid()));
            if (!isProjectMember) {
                throw new InvalidResponsibleException("O usuário responsável com ID " + responsibleUuid + " não é membro do projeto '" + project.getName() + "'.");
            }

             boolean isTeamMember = team.getMembers() != null && team.getMembers().stream()
                .anyMatch(membro -> membro != null && membro.uuid().equals(responsibleUuid));
             if (!isTeamMember) {
                throw new InvalidResponsibleException("O usuário responsável com ID " + responsibleUuid + " não é membro da equipe '" + team.getName() + "' (Inconsistência encontrada).");
             }
        }
    }

    private void manageUserTaskAssignment(Task task, String oldResponsibleUuid, String newResponsibleUuid) {
        if (!Objects.equals(oldResponsibleUuid, newResponsibleUuid)) {
            if (oldResponsibleUuid != null) {
                removeTaskFromUser(task.getUuid(), oldResponsibleUuid);
            }
            if (newResponsibleUuid != null) {
                addTaskToUser(task, newResponsibleUuid);
            }
        } else if (newResponsibleUuid != null) {
            updateTaskInUser(task, newResponsibleUuid);
        }
    }

    private void addTaskToUser(Task task, String userUuid) {
        userRepository.findById(userUuid).ifPresent(user -> {
            if (user.getTasks() == null) {
                user.setTasks(new ArrayList<>());
            }
            user.getTasks().removeIf(t -> t != null && task.getUuid().equals(t.uuid()));
            Task.TaskUser taskUser = task.toUserRef();
            user.getTasks().add(taskUser);
            userRepository.save(user);
        });
    }

    private void removeTaskFromUser(String taskUuid, String userUuid) {
        userRepository.findById(userUuid).ifPresent(user -> {
            if (user.getTasks() != null) {
                boolean removed = user.getTasks().removeIf(t -> t != null && taskUuid.equals(t.uuid())); 
                if (removed) {
                    userRepository.save(user);
                }
            }
        });
    }

    private void updateTaskInUser(Task task, String userUuid) {
        userRepository.findById(userUuid).ifPresent(user -> {
            if (user.getTasks() == null) return;
            int index = -1;
            for (int i = 0; i < user.getTasks().size(); i++) {
                 Task.TaskUser tu = user.getTasks().get(i);
                 if (tu != null && task.getUuid().equals(tu.uuid())) { 
                    index = i;
                    break;
                 }
            }

            if (index != -1) {
                user.getTasks().remove(index);
                user.getTasks().add(task.toUserRef()); 
                userRepository.save(user);
            }
        });
    }
}
