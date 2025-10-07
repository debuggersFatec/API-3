package com.api_3.api_3.service;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_3.api_3.exception.EquipeNotFoundException;
import com.api_3.api_3.exception.InvalidResponsibleException;
import com.api_3.api_3.exception.TaskValidationException;
import com.api_3.api_3.model.embedded.ResponsavelTask;
import com.api_3.api_3.model.embedded.TaskInfo;
import com.api_3.api_3.model.entity.Equipe;
import com.api_3.api_3.model.entity.Task;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.repository.EquipeRepository;
import com.api_3.api_3.repository.TaskRepository;
import com.api_3.api_3.repository.UserRepository;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EquipeRepository equipeRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Task createTask(Task newTask) {
        if (newTask.getTitle() == null || newTask.getTitle().trim().isEmpty()) {
            throw new TaskValidationException("O título da tarefa é obrigatório.");
        }
        if (newTask.getEquip_uuid() == null || newTask.getEquip_uuid().trim().isEmpty()) {
            throw new TaskValidationException("O ID da equipe é obrigatório para criar uma tarefa.");
        }

        validateResponsible(newTask);

        newTask.setUuid(UUID.randomUUID().toString());
        Task savedTask = taskRepository.save(newTask);

        equipeRepository.findById(savedTask.getEquip_uuid()).ifPresent(equipe -> {
            TaskInfo taskInfo = new TaskInfo();
            taskInfo.setUuid(savedTask.getUuid());
            taskInfo.setTitle(savedTask.getTitle());
            if (savedTask.getDue_date() != null) {
                taskInfo.setDue_date(savedTask.getDue_date().toString());
            }
            taskInfo.setStatus(savedTask.getStatus());
            taskInfo.setPrioridade(savedTask.getPriority());
            taskInfo.setEquipe_uuid(savedTask.getEquip_uuid());

            if (savedTask.getResponsible() != null) {
                ResponsavelTask responsavelTask = new ResponsavelTask();
                responsavelTask.setUuid(savedTask.getResponsible().getUuid());
                responsavelTask.setName(savedTask.getResponsible().getName());
                responsavelTask.setImg(savedTask.getResponsible().getUrl_img());
                taskInfo.setResponsavel(responsavelTask);
            }

            if (equipe.getTasks() == null) {
                equipe.setTasks(new ArrayList<>());
            }

            equipe.getTasks().add(taskInfo);
            equipeRepository.save(equipe);
        });

        if (savedTask.getResponsible() != null && savedTask.getResponsible().getUuid() != null) {
            addTaskToUser(savedTask, savedTask.getResponsible().getUuid());
        }

        return savedTask;
    }

    @Transactional
    public Optional<Task> updateTask(String uuid, Task updatedTaskData) {
        Task existingTask = taskRepository.findById(uuid)
                .orElse(null); 

        if (existingTask == null) {
            return Optional.empty();
        }
        
        String oldResponsibleUuid = (existingTask.getResponsible() != null) ? existingTask.getResponsible().getUuid() : null;
        String newResponsibleUuid = (updatedTaskData.getResponsible() != null) ? updatedTaskData.getResponsible().getUuid() : null;

        if (!Objects.equals(oldResponsibleUuid, newResponsibleUuid)) {
            validateResponsible(updatedTaskData);
        }

        existingTask.setTitle(updatedTaskData.getTitle());
        existingTask.setDescription(updatedTaskData.getDescription());
        existingTask.setDue_date(updatedTaskData.getDue_date());
        existingTask.setStatus(updatedTaskData.getStatus());
        existingTask.setPriority(updatedTaskData.getPriority());
        existingTask.setResponsible(updatedTaskData.getResponsible());
        
        Task savedTask = taskRepository.save(existingTask);

        updateTaskInEquipe(savedTask);

        if (!Objects.equals(oldResponsibleUuid, newResponsibleUuid)) {
            if (oldResponsibleUuid != null) {
                removeTaskFromUser(uuid, oldResponsibleUuid);
            }
            if (newResponsibleUuid != null) {
                addTaskToUser(savedTask, newResponsibleUuid);
            }
        } else if (newResponsibleUuid != null) {
            updateTaskInUser(savedTask, newResponsibleUuid);
        }

        return Optional.of(savedTask);
    }

    private void validateResponsible(Task task) {
        Equipe equipe = equipeRepository.findById(task.getEquip_uuid())
                .orElseThrow(() -> new EquipeNotFoundException("Equipe com ID " + task.getEquip_uuid() + " não encontrada."));

        if (task.getResponsible() != null && task.getResponsible().getUuid() != null) {
            String responsibleUuid = task.getResponsible().getUuid();

            userRepository.findById(responsibleUuid)
                    .orElseThrow(() -> new InvalidResponsibleException("Usuário responsável com ID " + responsibleUuid + " não encontrado."));

            boolean isMember = equipe.getMembros().stream()
                    .anyMatch(membro -> membro.getUuid().equals(responsibleUuid));

            if (!isMember) {
                throw new InvalidResponsibleException("O usuário responsável com ID " + responsibleUuid + " não é membro da equipe " + equipe.getName() + ".");
            }
        }
    }

    private void updateTaskInEquipe(Task task) {
        if (task.getEquip_uuid() != null && !task.getEquip_uuid().isEmpty()) {
            equipeRepository.findById(task.getEquip_uuid()).ifPresent(equipe -> {
                equipe.getTasks().stream()
                        .filter(taskInfo -> taskInfo.getUuid().equals(task.getUuid()))
                        .findFirst()
                        .ifPresent(taskInfo -> {
                            taskInfo.setTitle(task.getTitle());
                            taskInfo.setStatus(task.getStatus());
                            taskInfo.setPrioridade(task.getPriority());
                            taskInfo.setDue_date(task.getDue_date() != null ? task.getDue_date().toString() : null);

                            if (task.getResponsible() != null) {
                                ResponsavelTask responsavelTask = new ResponsavelTask();
                                responsavelTask.setUuid(task.getResponsible().getUuid());
                                responsavelTask.setName(task.getResponsible().getName());
                                responsavelTask.setImg(task.getResponsible().getUrl_img());
                                taskInfo.setResponsavel(responsavelTask);
                            } else {
                                taskInfo.setResponsavel(null);
                            }
                            equipeRepository.save(equipe);
                        });
            });
        }
    }

    private void updateTaskInUser(Task task, String userUuid) {
        userRepository.findById(userUuid).ifPresent(updateUserTasks(task));
    }

    private void addTaskToUser(Task task, String userUuid) {
        userRepository.findById(userUuid).ifPresent(user -> {
            if (user.getTasks() == null) {
                user.setTasks(new ArrayList<>());
            }
            if (user.getTasks().stream()
                    .noneMatch(t -> t instanceof TaskInfo && ((TaskInfo) t).getUuid().equals(task.getUuid()))) {
                TaskInfo taskInfoForUser = new TaskInfo();
                taskInfoForUser.setUuid(task.getUuid());
                taskInfoForUser.setTitle(task.getTitle());
                if (task.getDue_date() != null) {
                    taskInfoForUser.setDue_date(task.getDue_date().toString());
                }
                taskInfoForUser.setStatus(task.getStatus());
                taskInfoForUser.setPrioridade(task.getPriority());
                taskInfoForUser.setEquipe_uuid(task.getEquip_uuid());

                user.getTasks().add(taskInfoForUser);
                userRepository.save(user);
            }
        });
    }

    private void removeTaskFromUser(String taskUuid, String userUuid) {
        userRepository.findById(userUuid).ifPresent(user -> {
            if (user.getTasks() != null) {
                boolean removed = user.getTasks()
                        .removeIf(t -> t instanceof TaskInfo && ((TaskInfo) t).getUuid().equals(taskUuid));
                if (removed) {
                    userRepository.save(user);
                }
            }
        });
    }

    private Consumer<User> updateUserTasks(Task task) {
        return user -> {
            if (user.getTasks() != null) {
                user.getTasks().stream()
                        .filter(t -> t instanceof TaskInfo && ((TaskInfo) t).getUuid().equals(task.getUuid()))
                        .map(t -> (TaskInfo) t)
                        .findFirst()
                        .ifPresent(taskInfo -> {
                            taskInfo.setTitle(task.getTitle());
                            taskInfo.setStatus(task.getStatus());
                            taskInfo.setPrioridade(task.getPriority());
                            taskInfo.setDue_date(task.getDue_date() != null ? task.getDue_date().toString() : null);
                            userRepository.save(user);
                        });
            }
        };
    }

    @Transactional
    public Optional<Task> deleteTask(String uuid) {
        Optional<Task> taskOptional = taskRepository.findById(uuid);

        if (taskOptional.isEmpty()) {
            return Optional.empty();
        }

        Task task = taskOptional.get();

        if (task.getEquip_uuid() == null || task.getEquip_uuid().isEmpty()) {
            task.setStatus("excluida");
            Task updatedTask = taskRepository.save(task);
            return Optional.of(updatedTask);
        }

        equipeRepository.findById(task.getEquip_uuid()).ifPresent(equipe -> {
            Optional<TaskInfo> taskInfoOptional = equipe.getTasks().stream()
                    .filter(ti -> ti.getUuid().equals(uuid))
                    .findFirst();

            if (taskInfoOptional.isPresent()) {
                TaskInfo taskInfo = taskInfoOptional.get();

                equipe.getTasks().remove(taskInfo);

                if (equipe.getLixeira() == null) {
                    equipe.setLixeira(new ArrayList<>());
                }

                taskInfo.setStatus("excluida"); 

                equipe.getLixeira().add(taskInfo);

                equipeRepository.save(equipe);
            }
        });

        if (task.getResponsible() != null && task.getResponsible().getUuid() != null) {
            removeTaskFromUser(uuid, task.getResponsible().getUuid());
        }

        task.setStatus("excluida");
        Task updatedTask = taskRepository.save(task);

        return Optional.of(updatedTask);
    }
}