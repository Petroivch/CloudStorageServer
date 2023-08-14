package com.example.cloud.repository;

import com.example.cloud.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, String> {
    List<File> findAllByUserId(long userId);

    Optional<File> findByUserIdAndName(long userId, String name);

    void removeByUserIdAndName(long userId, String name);
}