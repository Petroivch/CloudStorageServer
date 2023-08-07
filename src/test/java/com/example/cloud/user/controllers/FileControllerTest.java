package com.example.cloud.user.controllers;

import com.example.cloud.controller.FileController;
import com.example.cloud.entity.User;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class FileControllerTest {

    @Autowired
    TestRestTemplate template;
    @InjectMocks
    private FileController fileController;
    @Autowired
    AuthenticationManager authManager;
    private String wrong_token = "Bearer wrongtoken";
    private String ok_token = "Bearer token";
    @Mock
    UserRepository userRepository;

    @Mock
    private FileService fileService;
    @InjectMocks
    @Autowired
    private static CloudRepository cloudRepository;

    private static final String FILE_BODY = "Hello, world";
    public static final MockMultipartFile MULTIPART_FILE =
            new MockMultipartFile("file", "file1.txt", MediaType.TEXT_PLAIN_VALUE, FILE_BODY.getBytes());


    /*    public static GenericContainer<?> app = new GenericContainer("app");

        @BeforeAll
        public static void setUp() {
            app.start();
        }*/
    public void settings() {
        fileService = new FileService(cloudRepository);
        fileController = new FileController(fileService);
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        "user@mail.ru", "qwerty"));
        User user = (User) authentication.getPrincipal();
        cloudRepository.tokenStorage.put(ok_token.substring(7), user);
    }

    @Test
    void doWithoutToken_GET() {
        ResponseEntity<String> response = template.getForEntity("/list", String.class);
        var expected = HttpStatus.UNAUTHORIZED.value();
        var actual = response.getStatusCodeValue();
        assertEquals(expected, actual);
    }

    @Test
    public void testUploadFileHttpStatusUnauthorized() throws IOException {
        var actualResult = fileController.uploadFile(wrong_token, MULTIPART_FILE);

        assertEquals(HttpStatus.UNAUTHORIZED, actualResult.getStatusCode());
    }
    @Test
    @ExtendWith(SpringExtension.class)
    public void testUploadFileHttpStatusOk() throws IOException {
        settings();
        var actualResult = fileController.uploadFile(ok_token, MULTIPART_FILE);

        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
    }
}