package org.example.mock.Repository.Admin;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.mock.Model.User;

import java.util.Optional;

public class AdminCustomRepositoryImplement{
    @PersistenceContext
    private EntityManager entityManager;



}
