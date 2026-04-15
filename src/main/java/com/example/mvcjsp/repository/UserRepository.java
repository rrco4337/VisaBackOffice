package com.example.mvcjsp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.mvcjsp.model.User;

public interface UserRepository extends JpaRepository<User, Long> {}
