package com.api_3.registration_service.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.api_3.registration_service.model.entity.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    boolean existsByEmail(String email);
    
    Optional<User> findByEmail(String email);
}