package com.api_3.api_3.repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.api_3.api_3.model.entity.Task;

@Repository
public interface TaskRepository extends MongoRepository<Task, String>{
    @Aggregation(pipeline = {
        "{ '$group' : { '_id' : '$status', 'count' : { '$sum' : 1 } } }"
    })
    List<Map<String, Object>> countByStatus();
    
    @Query("{ 'responsible.uuid' : ?0 }")
    List<Task> findByResponsibleUuid(String uuid);

    @Query(value = "{ 'teamUuid': ?0, 'responsible.uuid' : ?1 }")
    List<Task> findByTeamUuidAndResponsibleUuid(String teamUuid, String responsibleUuid);

    @Query(value = "{ 'projectUuid': ?0, 'responsible.uuid' : ?1 }")
    List<Task> findByProjectUuidAndResponsibleUuid(String projectUuid, String responsibleUuid);

    // Due-soon queries
    List<Task> findByDueDateBetweenAndStatusNot(Date startInclusive, Date endExclusive, Task.Status status);
}