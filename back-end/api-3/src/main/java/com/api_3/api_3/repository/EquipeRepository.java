package com.api_3.api_3.repository;
 
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.api_3.api_3.model.entity.Equipe;
 
@Repository
public interface EquipeRepository extends MongoRepository<Equipe, String> {
    Optional<Equipe> findByName(String name);
}