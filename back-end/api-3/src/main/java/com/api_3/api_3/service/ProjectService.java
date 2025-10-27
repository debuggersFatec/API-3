package com.api_3.api_3.service;

import java.util.List;

import com.api_3.api_3.dto.request.CreateProjectRequest;
import com.api_3.api_3.dto.request.UpdateProjectRequest;
import com.api_3.api_3.dto.response.ProjectResponse;

public interface ProjectService {

    List<ProjectResponse> listProjectsByTeam(String teamUuid, String userEmail);

    ProjectResponse getProjectDetails(String projectUuid, String userEmail);

    ProjectResponse createProject(String teamUuid, CreateProjectRequest request, String userEmail);

    ProjectResponse updateProject(String projectUuid, UpdateProjectRequest request, String userEmail);

    ProjectResponse archiveProject(String projectUuid, String userEmail);

    ProjectResponse activateProject(String projectUuid, String userEmail);

    ProjectResponse addMemberToProject(String projectUuid, String userUuidToAdd, String userEmail);
}