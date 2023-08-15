package com.example.cloud.user.controllers;

import com.example.cloud.controller.FileController;
import com.example.cloud.entity.File;
import com.example.cloud.entity.User;
import com.example.cloud.exception.InvalidTokenException;
import com.example.cloud.repository.CloudRepository;
import com.example.cloud.repository.FileRepository;
import com.example.cloud.repository.UserRepository;
import com.example.cloud.service.FileService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.testcontainers.containers.GenericContainer;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class FileControllerTest {

    @InjectMocks
    private FileController fileController;
    private String wrong_token = "Bearer wrongtoken";
    private String ok_token = "Bearer token";
    @Mock
    UserRepository userRepository;
    @Mock
    FileRepository fileRepository;

    @Mock
    private FileService fileService;
    @Mock
    private static CloudRepository cloudRepository;

    private static final String FILE_BODY = "Hello, world";
    public static final MockMultipartFile MULTIPART_FILE =
            new MockMultipartFile("file", "file1.txt", MediaType.TEXT_PLAIN_VALUE, FILE_BODY.getBytes());
    public static final File cloudFilePOJO;

    static {
        try {
            cloudFilePOJO = new File("file1.txt", MULTIPART_FILE.getContentType(), MULTIPART_FILE.getBytes(), MULTIPART_FILE.getSize());
            cloudFilePOJO.setUserId(1L);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static GenericContainer<?> app = new GenericContainer("app");
    @BeforeAll
    public void docSetUp() {
        app.start();
    }

    @BeforeEach
    public void setUp() {
        User user = new User("user@mail.ru", "qwerty");
        cloudRepository.tokenStorage.put(ok_token.substring(7), user);
    }

    @Test
    void testListHttpStatusUnauthorized() {
        try {
            fileController.getFiles(wrong_token);
            fail("My method didn't throw when I expected it to");
        } catch (InvalidTokenException invalidTokenException) {
        }
    }

    @Test
    void testListHttpStatusOk() {
        assertDoesNotThrow(() -> {
            fileController.getFiles(ok_token);
        });
    }

    @Test
    public void testUploadFileHttpStatusUnauthorized() throws IOException {
        var actualResult = fileController.uploadFile(wrong_token, MULTIPART_FILE);
        assertEquals(HttpStatus.UNAUTHORIZED, actualResult.getStatusCode());
    }

    @Test
    public void testUploadFileHttpStatusOk() throws IOException {
        var actualResult = fileController.uploadFile(ok_token, MULTIPART_FILE);
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
    }

    @Test
    public void testDownloadFileHttpStatusOk() throws IOException {
        fileController.uploadFile(ok_token, MULTIPART_FILE);
        Mockito.when(fileService.downloadFile("file1.txt")).thenReturn(cloudFilePOJO);
        var actualResult = fileController.downloadFile(ok_token, "file1.txt");
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        verify(fileService, times(1)).downloadFile("file1.txt");
    }

    @Test
    @ExtendWith(MockitoExtension.class)
    public void testDownloadFileHttpStatusUnauthorized() throws IOException {
        var actualResult = fileController.downloadFile(wrong_token, "file1.txt");
        assertEquals(HttpStatus.UNAUTHORIZED, actualResult.getStatusCode());
    }

    @Test
    public void testDeleteFileHttpStatusUnauthorized() throws IOException {
        var actualResult = fileController.deleteFile(wrong_token, "file1.txt");
        assertEquals(HttpStatus.UNAUTHORIZED, actualResult.getStatusCode());
    }

    @Test
    public void testDeleteFileHttpStatusOk() throws IOException {
        var actualResult = fileController.deleteFile(ok_token, "file1.txt");

        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
    }

    @Test
    public void testEditFileHttpStatusOk() throws IOException {
        var actualResult = fileController.editFile(ok_token, "file1.txt", "file_edit.txt");

        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
    }

    @Test
    public void testEditFileHttpStatusUnauthorized() throws IOException {
        var actualResult = fileController.editFile(wrong_token, "file1.txt", "file_edit");
        assertEquals(HttpStatus.UNAUTHORIZED, actualResult.getStatusCode());
    }
}