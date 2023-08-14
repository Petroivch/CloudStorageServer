package com.example.cloud.controller;

import com.example.cloud.entity.File;
import com.example.cloud.exception.InvalidTokenException;
import com.example.cloud.exception.ResponseMessage;
import com.example.cloud.repository.CloudRepository;
import com.example.cloud.service.FileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@Validated
public class FileController {
    private static final String FILE = "/file";
    private static final String LIST = "/list";

    private static final String UPLOAD = "/upload";

    private final FileService fileService;


    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(UPLOAD)
    public ResponseEntity<ResponseMessage> uploadFile(@RequestHeader("auth-token") String authToken, @RequestParam("file") MultipartFile file) {
        String message = "";
        if (!CloudRepository.tokenStorage.containsKey(authToken.substring(7))) {
            message = "Invalid auth-token";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessage(message));
        }

        try {
            String fileName;
            fileName = file.getOriginalFilename();
            File cloudFilePOJO = new File(fileName, file.getContentType(), file.getBytes(), file.getSize());
            fileService.uploadFile(file.getOriginalFilename(), cloudFilePOJO); //сохраняем файл в БД
            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (IOException e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + ". Error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @GetMapping(FILE)
    public ResponseEntity<byte[]> downloadFile(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String filename) {
        String message = "";
        if (!CloudRepository.tokenStorage.containsKey(authToken.substring(7))) {
            message = "Invalid auth-token";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).contentType(MediaType.parseMediaType(MediaType.APPLICATION_JSON_VALUE))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "null" + "\"")
                    .body(null);
        }
        try {
            File file = fileService.downloadFile(filename);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(file.getType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                    .body(file.getData());
        } catch (Exception e) {
            message = "Could not download the file: " + filename + ". Error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).contentType(MediaType.parseMediaType(MediaType.APPLICATION_JSON_VALUE))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "null" + "\"")
                    .body(message.getBytes());
        }
    }

    @GetMapping(LIST)
    public List<File> getFiles(@RequestHeader("auth-token") String authToken) {
        if (!CloudRepository.tokenStorage.containsKey(authToken.substring(7))) {
            throw new InvalidTokenException("Invalid auth-token");
        }

        try {
            CloudRepository.tokenStorage.containsKey(authToken.substring(7));
            return fileService.getFiles();
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid auth-token");
        }

    }

    @DeleteMapping(FILE)
    public ResponseEntity<?> deleteFile(@RequestHeader("auth-token") String token, @RequestParam("filename") String fileName) {
        String message = "";
        if (!CloudRepository.tokenStorage.containsKey(token.substring(7))) {
            message = "Invalid auth-token";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessage(message));
        }
        try {
            fileService.deleteFile(fileName);
            message = "Deleted the file successfully: " + fileName;
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception e) {
            message = "Can't delete the file: " + fileName + ". Error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @PutMapping(value = FILE)
    public ResponseEntity<?> editFile(@RequestHeader("auth-token") String authToken, @Valid @RequestParam("filename") String filename, @RequestParam("newname") String newname /*@RequestBody Map<String, String> bodyParams*/) {
        String message = "";
        if (!CloudRepository.tokenStorage.containsKey(authToken.substring(7))) {
            message = "Invalid auth-token";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessage(message));
        }
        try {
            fileService.renameFile(filename, newname);
            message = "Edit the filename successfully from " + filename + "to " + newname;
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception e) {
            message = "Can't edit the file: " + filename + ". Error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }
}
