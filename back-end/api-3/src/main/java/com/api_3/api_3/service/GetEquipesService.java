package com.api_3.api_3.service;

import org.springframework.stereotype.Service;

@Service
@Deprecated
public class GetEquipesService {
    public Object findAllForUser(String userEmail) {
        throw new UnsupportedOperationException("Legacy Equipe API removed. Use Teams services instead.");
    }
    public Object findByIdAndVerifyMembership(String uuid, String userEmail) {
        throw new UnsupportedOperationException("Legacy Equipe API removed. Use Teams services instead.");
    }
}