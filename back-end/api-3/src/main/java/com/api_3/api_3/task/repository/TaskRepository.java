package com.api_3.api_3.task.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.api_3.api_3.task.model.Task;

@Repository
public interface TaskRepository extends MongoRepository<Task, String>{

}
