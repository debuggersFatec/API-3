package com.api_3.api_3.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.api_3.api_3.model.entity.Projects;

public interface ProjectsRepository extends MongoRepository<Projects, String> {
	java.util.List<Projects> findByTeamUuid(String teamUuid);
}
