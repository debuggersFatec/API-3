package com.api_3.api_3.repository;

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
}