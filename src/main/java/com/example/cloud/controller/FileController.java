package com.example.cloud.controller;

import com.example.cloud.entity.File;
import com.example.cloud.exception.ResponseMessage;
import com.example.cloud.service.FileService;

import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Map;

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
   /* @PostMapping(value = FILE, produces = MediaType.APPLICATION_JSON_VALUE, consumes = {"multipart/form-data"})
    public ResponseEntity<?> uploadFile(@RequestHeader("auth-token") String authToken, @Valid @RequestParam String filename, @RequestBody MultipartFile file) throws IOException, IOException {
        fileService.uploadFile(authToken, filename, file);
        return new ResponseEntity<>(HttpStatus.OK);
    }*/

    @PostMapping(UPLOAD)
    public ResponseEntity<ResponseMessage> uploadFile(@RequestHeader("auth-token") String authToken, @RequestParam("file") MultipartFile file) {
        String message = "";
        try {
            //storageService.save(file); //сохраняем файл просто на диске
            String fileName;
            fileName = file.getOriginalFilename();
            File cloudFilePOJO = new File(fileName, file.getContentType(), file.getBytes(), file.getSize());
            fileService.uploadFile(authToken, file.getOriginalFilename(), cloudFilePOJO); //сохраняем файл в БД

            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + ". Error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @GetMapping(FILE)
    public ResponseEntity<byte[]> downloadFile(@RequestHeader("auth-token") String authToken, @RequestParam("filename") String filename) {
        File file = fileService.downloadFile(authToken, filename);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(file.getData());
    }

    @GetMapping(LIST)
    public List<File> getFiles(@RequestHeader("auth-token") String authToken) {
        return fileService.getFiles(authToken);
    }

    @DeleteMapping(FILE)
    public ResponseEntity<?> deleteFile(@RequestHeader("auth-token") String token, @RequestParam("filename") String fileName) {
        String message = "";
        try {
            fileService.deleteFile(token, fileName);

            message = "Deleted the file successfully: " + fileName;
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception e) {
            message = "Can't delete the file: " + fileName  + ". Error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @PutMapping(value = FILE)
    public ResponseEntity<?> editFile(@RequestHeader("auth-token") String authToken, @Valid @RequestParam("filename") String filename, @RequestParam("newname") String newname /*@RequestBody Map<String, String> bodyParams*/) {
        String message = "";
        try {
            fileService.renameFile(authToken, filename, newname);
            message = "Edit the filename successfully from "+ filename + "to " + newname;
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception e) {
            message = "Can't edit the file: " + filename  + ". Error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }
}
