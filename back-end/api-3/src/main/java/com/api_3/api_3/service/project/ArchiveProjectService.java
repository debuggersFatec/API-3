package com.api_3.api_3.service.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api_3.api_3.model.entity.Projects;
import com.api_3.api_3.model.entity.Task;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.repository.ProjectsRepository;
import com.api_3.api_3.repository.TaskRepository;
import com.api_3.api_3.repository.TeamsRepository;
import com.api_3.api_3.repository.UserRepository;

@Service
public class ArchiveProjectService {
    
    private final ProjectsRepository projectsRepository;
    private final TeamsRepository teamsRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Autowired
    public ArchiveProjectService(ProjectsRepository projectsRepository, TeamsRepository teamsRepository, TaskRepository taskRepository, UserRepository userRepository) {
        this.projectsRepository = projectsRepository;
        this.teamsRepository = teamsRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Projects execute(String projectUuid) {
        Projects project = projectsRepository.findById(projectUuid)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado com o UUID: " + projectUuid));
        var teamUuid = project.getTeamUuid();

        project.setActive(false);
        project.getTasks().forEach(taskRef -> {
            Task.TaskProject taskProject = taskRef;
            if (taskProject.getStatus() != Task.Status.COMPLETED) {
                // Create an updated Task object with responsible set to null and status set to NOT_STARTED
                Task taskRefUpdated = new Task();
                taskRefUpdated.setUuid(taskRef.uuid());
                taskRefUpdated.setTitle(taskRef.title());
                taskRefUpdated.setDueDate(taskRef.dueDate());
                taskRefUpdated.setStatus(Task.Status.NOT_STARTED);
                taskRefUpdated.setPriority(taskRef.priority());
                taskRefUpdated.setTeamUuid(taskRef.teamUuid());
                taskRefUpdated.setProjectUuid(taskRef.projectUuid());
                taskRefUpdated.setResponsible(null);

                // Update the taskRef in the project's task list
                project.getTasks().set(project.getTasks().indexOf(taskRef), taskRefUpdated.toProjectRef());
            }

            Task task = taskRepository.findById(taskRef.uuid())
                    .orElseThrow(() -> new RuntimeException("Tarefa não encontrada com o UUID: " + taskRef.uuid()));
            if (task.getStatus() != Task.Status.COMPLETED && task.getResponsible() != null) {
                var user = userRepository.findById(task.getResponsible().uuid());
                if (user.isPresent()) {
                    User u = user.get();
                    if (u.getTasks() != null) {
                        u.getTasks().removeIf(tu -> tu.getUuid().equals(task.getUuid()));
                        userRepository.save(u);
                    }
                }
                task.setResponsible(null);
            }
            if (task.getStatus() == Task.Status.IN_PROGRESS) {
                task.setStatus(Task.Status.NOT_STARTED);
            }
            taskRepository.save(task);
        });

        teamsRepository.findById(teamUuid).ifPresent(team -> {
            if (team.getProjects() == null) return;
            int index = -1;
            for (int i = 0; i < team.getProjects().size(); i++) {
                 Projects.ProjectRef projectRef = team.getProjects().get(i);
                 if (projectRef != null && project.getUuid().equals(projectRef.uuid())) { 
                    index = i;
                    break;
                 }
            }

            if (index != -1) {
                Projects.ProjectRef existingRef = team.getProjects().get(index);
                team.getProjects().set(index, new Projects.ProjectRef(
                    existingRef.uuid(),
                    existingRef.name(),
                    project.isActive()
                ));
                teamsRepository.save(team);
            }
        });

        project.getMembers().forEach(memberRef -> {
            User user = userRepository.findById(memberRef.getUuid())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o UUID: " + memberRef.getUuid()));
            user.getTeams().forEach(teamRef -> {
                for (int i = 0; i < teamRef.getProjects().size(); i++) {
                    Projects.ProjectRef projectRef = teamRef.getProjects().get(i);
                    if (projectRef.getUuid().equals(projectUuid)) {
                        Projects updatedProjectRef = new Projects();
                        updatedProjectRef.setUuid(projectRef.getUuid());
                        updatedProjectRef.setName(project.getName());
                        updatedProjectRef.setActive(false);
                        teamRef.getProjects().set(i, updatedProjectRef.toRef());
                        break;
                    }
                }
            });
            userRepository.save(user);
        });

        projectsRepository.save(project);
        return project;
    }
}
