package com.api_3.auth_service.repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.api_3.auth_service.model.entity.Task;

public interface TaskRepository extends MongoRepository<Task, String> {
    @Aggregation(pipeline = {
            "{ '$group' : { '_id' : '$status', 'count' : { '$sum' : 1 } } }"
    })
    List<Map<String, Object>> countByStatus();

    @Query("{ 'responsible.uuid' : ?0 }")
    List<Task> findByResponsibleUuid(String uuid);

    List<Task> findByDueDateBetweenAndStatusNot(Date start, Date end, Task.Status status);

    @Query(value = "{ 'teamUuid': ?0, 'responsible.uuid' : ?1 }")
    List<Task> findByTeamUuidAndResponsibleUuid(String teamUuid, String responsibleUuid);

    @Query(value = "{ 'projectUuid': ?0, 'responsible.uuid' : ?1 }")
    List<Task> findByProjectUuidAndResponsibleUuid(String projectUuid, String responsibleUuid);

    @Query("{ 'projectUuid': ?0 }")
    List<Task> findByProjectUuid(String projectUuid);
}
