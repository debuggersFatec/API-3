package com.api_3.auth_service.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.api_3.auth_service.model.entity.Projects;

public interface ProjectsRepository extends MongoRepository<Projects, String> {
    List<Projects> findByTeamUuid(String teamUuid);

    @Query("{ 'members.uuid' : ?0 }")
    List<Projects> findByMembersUuid(String userUuid);
}
