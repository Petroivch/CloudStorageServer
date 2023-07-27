package com.example.cloud;

import com.example.cloud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

public class ApplicationStartupRunner implements ApplicationRunner {
    @Autowired
    private UserRepository repo;

    @Override
    public void run(ApplicationArguments args) {

    }
}