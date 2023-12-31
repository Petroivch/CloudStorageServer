package com.example.cloud.user.controllers;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.GenericContainer;

import java.util.HashMap;
import java.util.Objects;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoginControllerTest {

    @Autowired
    TestRestTemplate template1;
    public static GenericContainer<?> app = new GenericContainer("app");

    @BeforeAll
    public static void docSetUp() {
        app.start();
    }



    @ParameterizedTest
    @ValueSource(strings = {"wuser1@mail.ru", "wruser@mail.ru", "wrouser@mail.ru", "wronuser@mail.ru", "wronguser@mail.ru"})
    void testLoggingWithWrongUsername_POST(String args) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("login", args);
        map.put("password", "qwerty");

        ResponseEntity<String> response = template1.postForEntity("/login", map, String.class);
        Assertions.assertEquals(response.getStatusCodeValue(), HttpStatus.UNAUTHORIZED.value());
    }

    void testLoggingWithNullValueMap_POST() {
        HashMap<Object, Object> map = new HashMap<>();
        map.put("login", null);
        map.put("password", null);

        ResponseEntity<String> response = template1.postForEntity("/login", map, String.class);
        Assertions.assertEquals(response.getStatusCodeValue(), HttpStatus.FORBIDDEN.value());
    }

    void testLoggingWithNullMap1_POST() {
        HashMap<Object, Object> map = new HashMap<>();
        map.put(null, null);
        map.put(null, null);


        try {
            ResponseEntity<String> response = template1.postForEntity("/login", map, String.class);
        } catch (Exception ex) {
            Assertions.assertTrue(ex.getMessage().contains("Could not write JSON"));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"us" +
            "er@mail.ru", "user1@mail.ru", "user2@mail.ru", "user3@mail.ru", "user4@mail.ru"})
    void testLoggingWithNullKey1_POST(String args) {
        HashMap<Object, Object> map = new HashMap<>();
        map.put(null, args);
        map.put(null, "12345");

        try {
            ResponseEntity<String> response = template1.postForEntity("/login", map, String.class);
        } catch (Exception ex) {
            Assertions.assertTrue(ex.getMessage().contains("Could not write JSON"));
        }
    }


    @ParameterizedTest
    @ValueSource(strings = {"qwvev", "ererr", "qwert", "qqqqq", "zzxcv", "aaaaa", "     ", "fvdvd"})
    void testLoggingWithWrongPassword_POST(String args) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("login", "user@mail.ru");
        map.put("password", args);

        ResponseEntity<String> response = template1.postForEntity("/login", map, String.class);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatusCodeValue());
    }

    @Test
    void successLogging_POST_expect200() {
        HashMap<String, String> map = new HashMap<>();
        map.put("login", "user@mail.ru");
        map.put("password", "qwerty");

        ResponseEntity<String> response = template1.postForEntity("/login", map, String.class);
        Assertions.assertEquals(response.getStatusCodeValue(), HttpStatus.OK.value());
    }

    @Test
    void successLogging_POST_expectNotEmptyToken() {
        HashMap<String, String> map = new HashMap<>();
        map.put("login", "user@mail.ru");
        map.put("password", "qwerty");

        ResponseEntity<String> response = template1.postForEntity("/login", map, String.class);
        Assertions.assertTrue(Objects.requireNonNull(response.getBody()).contains("accessToken"));
    }


}

