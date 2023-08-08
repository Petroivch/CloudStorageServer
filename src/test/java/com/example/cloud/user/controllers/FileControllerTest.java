package com.example.cloud.user.controllers;

import com.example.cloud.controller.FileController;
import com.example.cloud.entity.File;
import com.example.cloud.entity.User;
import com.example.cloud.exception.InvalidTokenException;
import com.example.cloud.repository.CloudRepository;
import com.example.cloud.repository.FileRepository;
import com.example.cloud.repository.UserRepository;
import com.example.cloud.service.FileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
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


    /*    public static GenericContainer<?> app = new GenericContainer("app");

        @BeforeAll
        public static void setUp() {
            app.start();
        }*/
    @Test
    void testListHttpStatusUnauthorized() {
        settings();
        try {
            fileController.getFiles(wrong_token);
            fail( "My method didn't throw when I expected it to" );
        } catch (InvalidTokenException invalidTokenException) {
        }
    }
    @Test
    void testListHttpStatusOk() {
        settings();
        assertDoesNotThrow(() -> {
            fileController.getFiles(ok_token);
        });
    }
    public void settings() {
        User user = new User("user@mail.ru", "qwerty");
        cloudRepository.tokenStorage.put(ok_token.substring(7), user);
    }


    @Test
    public void testUploadFileHttpStatusUnauthorized() throws IOException {
        var actualResult = fileController.uploadFile(wrong_token, MULTIPART_FILE);

        assertEquals(HttpStatus.UNAUTHORIZED, actualResult.getStatusCode());
    }
    @Test
    @ExtendWith(MockitoExtension.class)
    public void testUploadFileHttpStatusOk() throws IOException {
        settings();
        var actualResult = fileController.uploadFile(ok_token, MULTIPART_FILE);

        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
    }
    @Test
    @ExtendWith(MockitoExtension.class)
    public void testDownloadFileHttpStatusOk() throws IOException {
        settings();
        fileController.uploadFile(ok_token,MULTIPART_FILE);
        var actualResult = fileController.downloadFile(ok_token, "file1.txt");
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        fileController.deleteFile(ok_token, "file1.txt");
    }
    @Test
    @ExtendWith(MockitoExtension.class)
    public void testDownloadFileHttpStatusUnauthorized() throws IOException {
        settings();
        var actualResult = fileController.downloadFile(wrong_token, "file1.txt");
        assertEquals(HttpStatus.UNAUTHORIZED, actualResult.getStatusCode());
    }
    @Test
    @ExtendWith(MockitoExtension.class)
    public void testDeleteFileHttpStatusUnauthorized() throws IOException {
        settings();
        var actualResult = fileController.deleteFile(wrong_token, "file1.txt");
        assertEquals(HttpStatus.UNAUTHORIZED, actualResult.getStatusCode());
    }
    @Test
    @ExtendWith(MockitoExtension.class)
    public void testDeleteFileHttpStatusOk() throws IOException {
        settings();
        var actualResult = fileController.deleteFile(ok_token, "file1.txt");

        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
    }
    @Test
    @ExtendWith(MockitoExtension.class)
    public void testEditFileHttpStatusOk() throws IOException {
        settings();
        var actualResult = fileController.editFile(ok_token, "file1.txt", "file_edit.txt");

        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
    }
    @Test
    @ExtendWith(MockitoExtension.class)
    public void testEditFileHttpStatusUnauthorized() throws IOException {
        settings();
        var actualResult = fileController.editFile(wrong_token, "file1.txt", "file_edit");
        assertEquals(HttpStatus.UNAUTHORIZED, actualResult.getStatusCode());
    }
}