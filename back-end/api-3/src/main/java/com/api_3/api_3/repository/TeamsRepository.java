package com.api_3.api_3.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.api_3.api_3.model.entity.Teams;

public interface TeamsRepository extends MongoRepository<Teams, String> {
}
