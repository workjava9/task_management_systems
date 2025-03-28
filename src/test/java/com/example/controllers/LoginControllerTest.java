package com.example.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.example.dto.LoginDTO;
import com.example.dto.UserDTO;
import com.example.exception.AuthenticationFailException;
import com.example.service.UserService;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_CLASS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = {"classpath:before_tests.sql"}, executionPhase = BEFORE_TEST_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LoginControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private Filter springSecurityFilterChain;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).addFilters(springSecurityFilterChain).build();
    }

    @Test
    void loginTest() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setMail("base@mail.ru");
        loginDTO.setPassword("password");

        MvcResult mvcResult = this.mockMvc.perform(post("/login").content(objectMapper.writeValueAsString(loginDTO)).contentType("application/JSON")).andDo(print()).andExpect(status().isOk()).andReturn();
    }

    @Test
    void loginFailTest() throws Exception {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setMail("new-user@mail.ru");
        loginDTO.setPassword("wrong_password");

        MvcResult mvcResult = this.mockMvc.perform(post("/login").content(objectMapper.writeValueAsString(loginDTO)).contentType("application/JSON")).andDo(print()).andExpect(result -> assertTrue(result.getResolvedException() instanceof AuthenticationFailException)).andReturn();
    }

    @Test
    void registrationTest() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("new-user");
        userDTO.setMail("new-user@mail.ru");
        userDTO.setPassword("password");

        MvcResult mvcResult = this.mockMvc.perform(post("/login/register").content(objectMapper.writeValueAsString(userDTO)).contentType("application/JSON")).andDo(print()).andExpect(status().isOk()).andReturn();

        assertEquals("new-user@mail.ru", userService.getUserById(4).get().getMail());
    }
}