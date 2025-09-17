package com.api_3.api_3.equipe.repository;
 
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.api_3.api_3.equipe.model.Equipe;
 
@Repository
public interface EquipeRepository extends MongoRepository<Equipe, String> {
    Optional<Equipe> findByName(String name);
}