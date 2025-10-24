package com.api_3.api_3.service;

import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.ArrayList; 
import java.util.stream.Collectors; 

import com.api_3.api_3.dto.request.UpdateUserRequest;
import com.api_3.api_3.model.entity.User;
import com.api_3.api_3.model.entity.Teams; 
import com.api_3.api_3.model.entity.Projects;
import com.api_3.api_3.model.entity.Task; 
import com.api_3.api_3.repository.UserRepository;
import com.api_3.api_3.repository.TeamsRepository; 
import com.api_3.api_3.repository.ProjectsRepository;
import com.api_3.api_3.repository.TaskRepository; 


@Service
public class UpdateUserService {

    private final UserRepository userRepository;
    private final TeamsRepository teamsRepository;
    private final ProjectsRepository projectsRepository; 
    private final TaskRepository taskRepository; 

    @Autowired
    public UpdateUserService(UserRepository userRepository,
                             TeamsRepository teamsRepository,
                             ProjectsRepository projectsRepository,
                             TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.teamsRepository = teamsRepository;
        this.projectsRepository = projectsRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional
    public User updateCurrentUser(UpdateUserRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new RuntimeException("Usuário não autenticado.");
        }
        String email = auth.getName();
        User user = userRepository.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado.")); // Considerar UserNotFoundException

        boolean nameChanged = false;
        boolean imgChanged = false;

        if (request.getName() != null && !request.getName().isBlank() && !request.getName().trim().equals(user.getName())) {
            user.setName(request.getName().trim());
            nameChanged = true;
        }
        if (request.getImg() != null && !request.getImg().isBlank() && !request.getImg().trim().equals(user.getImg())) {
            user.setImg(request.getImg().trim());
            imgChanged = true;
        }
        // Se nada mudou, retorna o usuário sem salvar ou cascatear
        if (!nameChanged && !imgChanged) {
            return user;
        }

        // Salva a atualização principal no documento do usuário
        User updatedUser = userRepository.save(user);

        // Dispara as atualizações em cascata SE algo mudou
        updateUserReferences(updatedUser);

        return updatedUser;
    }

    private void updateUserReferences(User updatedUser) {
        String userId = updatedUser.getUuid();
        User.UserRef updatedUserRef = updatedUser.toRef(); 

        // Atualizar em Teams
        List<Teams> teamsToUpdate = teamsRepository.findByMembersUuid(userId);
        List<Teams> modifiedTeams = new ArrayList<>();
        for (Teams team : teamsToUpdate) {
            boolean teamModified = false;
            List<User.UserRef> updatedMembers = team.getMembers().stream()
                .map(member -> {
                    if (userId.equals(member.uuid())) {
                        
                        return updatedUserRef;
                    }
                    return member; 
                })
                .collect(Collectors.toList());

            // Verifica se a lista realmente mudou para evitar salvamentos desnecessários
            if (!updatedMembers.equals(team.getMembers())) {
                 team.setMembers(updatedMembers);
                 modifiedTeams.add(team);
            }
        }
         if (!modifiedTeams.isEmpty()) {
            teamsRepository.saveAll(modifiedTeams);
        }


        // Atualizar em Projects
        List<Projects> projectsToUpdate = projectsRepository.findByMembersUuid(userId);
         List<Projects> modifiedProjects = new ArrayList<>();
        for (Projects project : projectsToUpdate) {
             List<User.UserRef> updatedMembers = project.getMembers().stream()
                .map(member -> userId.equals(member.uuid()) ? updatedUserRef : member)
                .collect(Collectors.toList());

             if (!updatedMembers.equals(project.getMembers())) {
                 project.setMembers(updatedMembers);
                 modifiedProjects.add(project);
             }
        }
         if (!modifiedProjects.isEmpty()) {
            projectsRepository.saveAll(modifiedProjects);
        }

        //  Atualizar em Tasks 
        List<Task> tasksToUpdate = taskRepository.findByResponsibleUuid(userId);
         List<Task> modifiedTasks = new ArrayList<>();
        for (Task task : tasksToUpdate) {
            task.setResponsible(updatedUserRef);
             modifiedTasks.add(task);
        }
         if (!modifiedTasks.isEmpty()) {
            taskRepository.saveAll(modifiedTasks);
        }
    }
}