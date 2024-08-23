package com.fls.animecommunity.animesanctuary.controller.member;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional // Ensure each test runs independently with its own transaction
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldRegisterMemberSuccessfully() throws Exception {
        String memberJson = """
            {
                "username": "testuser",
                "password": "securepassword",
                "name": "John Doe",
                "email": "johndoe@example.com",
                "gender": "MALE",
                "birth": "1990-01-01"
            }
        """;

        // Perform a POST request to register a new member
        mockMvc.perform(post("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(memberJson))
                .andExpect(status().isOk()) // Expect a 200 OK status
                .andExpect(jsonPath("$.id").isNumber()) // Validate that the response contains a numeric ID
                .andExpect(jsonPath("$.username").value("testuser")) // Validate the username in the response
                .andExpect(jsonPath("$.email").value("johndoe@example.com")); // Validate the email in the response
    }

    @Test
    public void shouldFailRegistrationWhenUsernameExists() throws Exception {
        // Register an existing user
        String existingUserJson = """
            {
                "username": "existinguser",
                "password": "password123",
                "name": "Existing User",
                "email": "existing@example.com",
                "gender": "MALE",
                "birth": "1990-01-01"
            }
        """;
        mockMvc.perform(post("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(existingUserJson))
                .andExpect(status().isOk()); // Expect a 200 OK status for the first registration

        // Attempt to register a new user with the same username
        String newUserJson = """
            {
                "username": "existinguser",
                "password": "newpassword123",
                "name": "New User",
                "email": "newuser@example.com",
                "gender": "FEMALE",
                "birth": "1991-01-01"
            }
        """;
        mockMvc.perform(post("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(newUserJson))
                .andExpect(status().isBadRequest());  // Expect a 400 Bad Request status due to duplicate username
    }

    @Test
    public void shouldLoginSuccessfully() throws Exception {
        // Register a user for login
        String memberJson = """
            {
                "username": "loginuser",
                "password": "securepassword",
                "name": "Login User",
                "email": "loginuser@example.com",
                "gender": "MALE",
                "birth": "1990-01-01"
            }
        """;
        mockMvc.perform(post("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(memberJson))
                .andExpect(status().isOk()); // Expect a 200 OK status for registration

        // Attempt to log in with the registered user
        String loginJson = """
            {
                "username": "loginuser",
                "password": "securepassword"
            }
        """;
        mockMvc.perform(post("/api/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isOk()); // Expect a 200 OK status for successful login
    }

    @Test
    public void shouldFailLoginWithWrongPassword() throws Exception {
        // Register a user for login
        String memberJson = """
            {
                "username": "loginuser",
                "password": "securepassword",
                "name": "Login User",
                "email": "loginuser@example.com",
                "gender": "MALE",
                "birth": "1990-01-01"
            }
        """;
        mockMvc.perform(post("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(memberJson))
                .andExpect(status().isOk()); // Expect a 200 OK status for registration

        // Attempt to log in with the wrong password
        String loginJson = """
            {
                "username": "loginuser",
                "password": "wrongpassword"
            }
        """;
        mockMvc.perform(post("/api/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isUnauthorized()); // Expect a 401 Unauthorized status for wrong password
    }

    @Test
    public void shouldDeleteMemberSuccessfully() throws Exception {
        // Register a user to be deleted
        String memberJson = """
            {
                "username": "loginuser",
                "password": "securepassword",
                "name": "Login User",
                "email": "loginuser@example.com",
                "gender": "MALE",
                "birth": "1990-01-01"
            }
        """;
        mockMvc.perform(post("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(memberJson))
                .andExpect(status().isOk()); // Expect a 200 OK status for registration

        // Log in the user to establish a session
        String loginJson = """
            {
                "username": "loginuser",
                "password": "securepassword"
            }
        """;
        mockMvc.perform(post("/api/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isOk()); // Expect a 200 OK status for successful login

        // Attempt to delete the registered user (ID should be fetched dynamically if necessary)
        mockMvc.perform(post("/api/members/delete/{id}", 1L)  // Use the correct user ID
                .param("username", "loginuser")
                .param("password", "securepassword"))
                .andExpect(status().isOk()); // Expect a 200 OK status for successful deletion
    }
}
