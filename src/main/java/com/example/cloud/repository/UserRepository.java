package com.example.cloud.repository;

import java.util.Optional;

import com.example.cloud.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	
	Optional<User> findByLogin(String login);
	
}
