package com.api_3.api_3.task.service;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_3.api_3.equipe.model.Equipe;
import com.api_3.api_3.equipe.model.ResponsavelTask;
import com.api_3.api_3.equipe.model.TaskInfo;
import com.api_3.api_3.equipe.repository.EquipeRepository;
import com.api_3.api_3.task.model.Task;
import com.api_3.api_3.task.repository.TaskRepository;
import com.api_3.api_3.user.model.User;
import com.api_3.api_3.user.repository.UserRepository;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EquipeRepository equipeRepository;

    @Autowired
    private UserRepository userRepository;

    // ... (método createTask inalterado) ...
    @Transactional
    public Task createTask(Task newTask) {
        newTask.setUuid(UUID.randomUUID().toString());
        Task savedTask = taskRepository.save(newTask);

        // Adiciona a tarefa à lista de tasks da equipe
        if (savedTask.getEquip_uuid() != null && !savedTask.getEquip_uuid().isEmpty()) {
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
        }

        // Adiciona a tarefa à lista de tasks do usuário responsável
        if (savedTask.getResponsible() != null && savedTask.getResponsible().getUuid() != null) {
            userRepository.findById(savedTask.getResponsible().getUuid()).ifPresent(user -> {
                if (user.getTasks() == null) {
                    user.setTasks(new ArrayList<>());
                }
                TaskInfo taskInfoForUser = new TaskInfo();
                taskInfoForUser.setUuid(savedTask.getUuid());
                taskInfoForUser.setTitle(savedTask.getTitle());
                if (savedTask.getDue_date() != null) {
                    taskInfoForUser.setDue_date(savedTask.getDue_date().toString());
                }
                taskInfoForUser.setStatus(savedTask.getStatus());
                taskInfoForUser.setPrioridade(savedTask.getPriority());
                taskInfoForUser.setEquipe_uuid(savedTask.getEquip_uuid());

                user.getTasks().add(taskInfoForUser);
                userRepository.save(user);
            });
        }

        return savedTask;
    }

    @Transactional
    public Optional<Task> updateTask(String uuid, Task updatedTaskData) {
        Optional<Task> taskOptional = taskRepository.findById(uuid);
        if (taskOptional.isEmpty()) {
            return Optional.empty();
        }

        Task existingTask = taskOptional.get();

        // Lógica para lidar com a mudança de responsável
        String oldResponsibleUuid = (existingTask.getResponsible() != null) ? existingTask.getResponsible().getUuid()
                : null;
        String newResponsibleUuid = (updatedTaskData.getResponsible() != null)
                ? updatedTaskData.getResponsible().getUuid()
                : null;

        // Atualiza os campos da tarefa
        existingTask.setTitle(updatedTaskData.getTitle());
        existingTask.setDescription(updatedTaskData.getDescription());
        existingTask.setDue_date(updatedTaskData.getDue_date());
        existingTask.setStatus(updatedTaskData.getStatus());
        existingTask.setPriority(updatedTaskData.getPriority());
        existingTask.setResponsible(updatedTaskData.getResponsible());
        // Adicionar outros campos que possam ser atualizados

        Task savedTask = taskRepository.save(existingTask);

        // Atualiza a tarefa na lista da equipe
        updateTaskInEquipe(savedTask);

        // Se o responsável mudou, remove a tarefa do antigo e adiciona ao novo
        if (!Objects.equals(oldResponsibleUuid, newResponsibleUuid)) {
            if (oldResponsibleUuid != null) {
                removeTaskFromUser(uuid, oldResponsibleUuid);
            }
            if (newResponsibleUuid != null) {
                addTaskToUser(savedTask, newResponsibleUuid);
            }
        } else if (newResponsibleUuid != null) {
            // Se o responsável é o mesmo, apenas atualiza a tarefa
            updateTaskInUser(savedTask, newResponsibleUuid);
        }

        return Optional.of(savedTask);
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
            // Evita adicionar duplicados
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

        // Se a tarefa não pertence a uma equipe, apenas mude o status
        if (task.getEquip_uuid() == null || task.getEquip_uuid().isEmpty()) {
            task.setStatus("excluida");
            Task updatedTask = taskRepository.save(task);
            return Optional.of(updatedTask);
        }

        // Lógica para mover a tarefa para a lixeira da equipe
        equipeRepository.findById(task.getEquip_uuid()).ifPresent(equipe -> {
            Optional<TaskInfo> taskInfoOptional = equipe.getTasks().stream()
                    .filter(ti -> ti.getUuid().equals(uuid))
                    .findFirst();

            if (taskInfoOptional.isPresent()) {
                TaskInfo taskInfo = taskInfoOptional.get();

                // Remove da lista de tarefas ativas
                equipe.getTasks().remove(taskInfo);

                // Adiciona na lixeira da equipe
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