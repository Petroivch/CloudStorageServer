package com.example.cloud.service;

import com.example.cloud.entity.File;
import com.example.cloud.exception.StorageException;
import com.example.cloud.repository.CloudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class FileService {
    private final CloudRepository cloudRepository;

    @Autowired
    public FileService(CloudRepository cloudRepository) {
        this.cloudRepository = cloudRepository;
    }

    public File downloadFile(String fileName) {
        return cloudRepository.downloadFile(fileName).orElseThrow(() -> new StorageException("Error download file " + fileName));
    }

    public List<File> getFiles() {
        return cloudRepository.getFiles().orElseThrow(() -> new StorageException("Error getting file list"));
    }

    public void uploadFile(String fileName, File cloudFilePOJO) throws IOException {

        cloudRepository.uploadFile(cloudFilePOJO).orElseThrow(() -> new StorageException("Couldn't save the file " + fileName));
    }

    public void deleteFile(String fileName) {
        cloudRepository.deleteFile(fileName);
    }

    public void renameFile(String fileName, String newFileName) {
        cloudRepository.renameFile(fileName, newFileName).orElseThrow(() -> new StorageException("Error edit file " + fileName));
    }

}