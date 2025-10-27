package com.api_3.api_3.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import com.api_3.api_3.model.entity.Projects;
import java.util.List; 


public interface ProjectsRepository extends MongoRepository<Projects, String> {
    List<Projects> findByTeamUuid(String teamUuid); 

    @Query("{ 'members.uuid' : ?0 }")
    List<Projects> findByMembersUuid(String userUuid);
}