package com.example.cloud.service;

import com.example.cloud.entity.File;
import com.example.cloud.exception.StorageException;
import com.example.cloud.repository.CloudRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class FileService {
    private final CloudRepository cloudRepository;

    @Autowired
    public FileService(CloudRepository cloudRepository) {
        this.cloudRepository = cloudRepository;
    }

    public File downloadFile(String fileName) {
        log.info("Service - downloading file " + fileName);
        return cloudRepository.downloadFile(fileName).orElseThrow(() -> new StorageException("Error download file " + fileName));
    }

    public List<File> getFiles() {
        log.info("Service - get all files");
        return cloudRepository.getFiles().orElseThrow(() -> new StorageException("Error getting file list"));
    }

    public void uploadFile(String fileName, File cloudFilePOJO) throws IOException {
        log.info("Service - uploading file " + fileName);
        cloudRepository.uploadFile(cloudFilePOJO).orElseThrow(() -> new StorageException("Couldn't save the file " + fileName));
    }

    public void deleteFile(String fileName) {
        log.info("Service - deleting file " + fileName);
        cloudRepository.deleteFile(fileName);
    }

    public void renameFile(String fileName, String newFileName) {
        log.info("Service - renaming file " + fileName);
        cloudRepository.renameFile(fileName, newFileName).orElseThrow(() -> new StorageException("Error edit file " + fileName));
    }

}