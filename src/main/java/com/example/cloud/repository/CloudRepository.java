package com.example.cloud.repository;

import javax.transaction.Transactional;

import com.example.cloud.entity.User;
import com.example.cloud.exception.StorageException;
import com.example.cloud.entity.File;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


import static java.util.Optional.ofNullable;


@Data
@Transactional
@Repository
public class CloudRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(CloudRepository.class);
    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    @Autowired
    public CloudRepository(UserRepository userRepository,
                           FileRepository fileRepository) {
        this.userRepository = userRepository;
        this.fileRepository = fileRepository;
    }

    public static Map<String, UserDetails> tokenStorage = new ConcurrentHashMap<>();

    public void login(String authToken, User userPrincipal) {
        LOGGER.info("Success login");
        tokenStorage.put(authToken, userPrincipal);
    }

    public Optional<UserDetails> logout(String authToken) {
        LOGGER.info("Success logout");
        return ofNullable(tokenStorage.remove(authToken));
    }

    public Optional<File> uploadFile(File cloudFile) {

        if (!tokenStorage.isEmpty()) {
            Optional<String> token = tokenStorage.keySet().stream().findFirst();
            String result = token
                    .map(Object::toString)
                    .orElse("");
            Optional<Long> userId = getUserId((result));
                cloudFile.setUserId(userId.get());

            LOGGER.info("Upload file " + cloudFile.getName() + " successfuly");
            return Optional.of(fileRepository.save(cloudFile));
        } else {
            LOGGER.error("Invalid auth-token");
            throw new StorageException("Invalid auth-token");
        }
    }

    public void deleteFile(String fileName) {
        if (!tokenStorage.isEmpty()) {
            Optional<String> token = tokenStorage.keySet().stream().findFirst();
            String result = token
                    .map(Object::toString)
                    .orElse("");
            Optional<Long> userId = getUserId((result));

            LOGGER.info("Remove file " + fileName);
            fileRepository.removeByUserIdAndName(userId.get(), fileName);
        } else {
            LOGGER.error("Invalid auth-token");
            throw new StorageException("Invalid auth-token");
        }
    }

    public Optional<File> downloadFile(String fileName) {

        if (!tokenStorage.isEmpty()) {
            Optional<String> token = tokenStorage.keySet().stream().findFirst();
            String result = token
                    .map(Object::toString)
                    .orElse("");
            Optional<Long> userId = getUserId((result));
            LOGGER.info("Download file " + fileName);
            return fileRepository.findByUserIdAndName(userId.get(), fileName);
        } else {
            LOGGER.error("Invalid auth-token");
            throw new StorageException("Invalid auth-token");
        }
    }

    public Optional<File> renameFile(String fileName, String newFileName) {
        if (!tokenStorage.isEmpty()) {
            Optional<String> token = tokenStorage.keySet().stream().findFirst();
            String result = token
                    .map(Object::toString)
                    .orElse("");
            Optional<Long> userId = getUserId((result));
            Optional<File> cloudFile = fileRepository.findByUserIdAndName(userId.get(), fileName);
            cloudFile.ifPresent(file -> file.setName(newFileName));
            LOGGER.info("Rename file " + fileName);
            return Optional.of(fileRepository.save(Objects.requireNonNull(cloudFile.orElse(null))));
        } else {
            LOGGER.error("Invalid auth-token");
            throw new StorageException("Invalid auth-token");
        }
    }


    public Optional<List<File>> getFiles() {

        if (!tokenStorage.isEmpty()) {
            Optional<String> token = tokenStorage.keySet().stream().findFirst();
            String result = token
                    .map(Object::toString)
                    .orElse("");
            Optional<Long> userId = getUserId((result));
            LOGGER.info("Get all files");
            return ofNullable(fileRepository.findAllByUserId(userId.get()));
        } else {
            LOGGER.error("Invalid auth-token");
            throw new StorageException("Invalid auth-token");
        }
    }

    private Optional<Long> getUserId(String authToken) {
        UserDetails user = tokenStorage.get(authToken);
        return user != null ? ofNullable(Objects.requireNonNull(userRepository.findByLogin(user.getUsername()).orElse(null)).getId()) : Optional.empty();
    }
}