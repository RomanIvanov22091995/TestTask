package com.example.testtask.controller;

import com.example.testtask.TestTaskApplication;
import model.Account;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.Testcontainers;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import repository.UserRepository;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Testcontainers
@SpringBootTest(classes = TestTaskApplication.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    private MockMvc mockMvc;
    private UserRepository userRepository;

    public UserControllerTest(MockMvc mockMvc, UserRepository userRepository) {
        this.mockMvc = mockMvc;
        this.userRepository = userRepository;
    }

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        User user = new User();
        user.setId(1L); // Задайте ID вручную, если используете in-memory БД
        user.setName("testuser");
        user.setAccount(new Account());
        user.getAccount().setBalance(new BigDecimal("100.00"));
        userRepository.save(user);
    }

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:tc:postgresql://localhost:5432/testdb");
        registry.add("spring.datasource.username", () -> "test");
        registry.add("spring.datasource.password", () -> "test");
    }

    @Test
    public void getUser_ReturnsUser() throws Exception {
        mockMvc.perform(get("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }
}
