package com.api_3.api_3.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.api_3.api_3.model.entity.Teams;

public interface TeamsRepository extends MongoRepository<Teams, String> {
    @Query("{ 'members.uuid' : ?0 }")
    List<Teams> findByMembersUuid(String userUuid);
}
