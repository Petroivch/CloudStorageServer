package com.example.cloud.user.controllers;

import com.example.cloud.controller.FileController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
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
import org.testcontainers.containers.GenericContainer;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
public class FileControllerTest {

    @Autowired
    TestRestTemplate template;
    @InjectMocks
    private FileController fileController;


    private static final String FILE_BODY = "Hello, world";
    public static final MockMultipartFile MULTIPART_FILE =
            new MockMultipartFile("file", "file1.txt", MediaType.TEXT_PLAIN_VALUE, FILE_BODY.getBytes());
/*    public static GenericContainer<?> app = new GenericContainer("app");

    @BeforeAll
    public static void setUp() {
        app.start();
    }*/

    @Test
    void doWithoutToken_GET() {
        ResponseEntity<String> response = template.getForEntity("/list", String.class);
        var expected = HttpStatus.UNAUTHORIZED.value();
        var actual = response.getStatusCodeValue();
        assertEquals(expected, actual);
    }

    @Test
    public void testUploadFileHttpStatusUnauthorized() throws IOException {
        var actualResult = fileController.uploadFile("wrong-token", MULTIPART_FILE);

        assertEquals(HttpStatus.UNAUTHORIZED, actualResult.getStatusCode());
    }
}