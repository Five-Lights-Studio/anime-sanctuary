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
@Transactional // 각 테스트가 독립적으로 실행되도록 트랜잭션을 사용
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

        mockMvc.perform(post("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(memberJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("johndoe@example.com"));
    }

    @Test
    public void shouldFailRegistrationWhenUsernameExists() throws Exception {
        // 사전 조건: 이미 존재하는 사용자 등록
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
                .andExpect(status().isOk());

        // 동일한 사용자명을 가진 사용자 등록 시도
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
                .andExpect(status().isBadRequest());  // 이미 존재하는 사용자명으로 등록 시 실패해야 함
    }

    @Test
    public void shouldLoginSuccessfully() throws Exception {
        // 사용자 등록
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
                .andExpect(status().isOk());

        // 로그인 시도
        String loginJson = """
            {
                "username": "loginuser",
                "password": "securepassword"
            }
        """;
        mockMvc.perform(post("/api/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldFailLoginWithWrongPassword() throws Exception {
        // 사용자 등록
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
                .andExpect(status().isOk());

        // 잘못된 비밀번호로 로그인 시도
        String loginJson = """
            {
                "username": "loginuser",
                "password": "wrongpassword"
            }
        """;
        mockMvc.perform(post("/api/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldDeleteMemberSuccessfully() throws Exception {
        // 사용자 등록
        String memberJson = """
            {
                "username": "deletetest",
                "password": "deletepassword",
                "name": "Delete Test",
                "email": "deletetest@example.com",
                "gender": "MALE",
                "birth": "1990-01-01"
            }
        """;
        mockMvc.perform(post("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(memberJson))
                .andExpect(status().isOk());

        // 로그인 (세션 설정을 위해)
        String loginJson = """
            {
                "username": "deletetest",
                "password": "deletepassword"
            }
        """;
        mockMvc.perform(post("/api/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isOk());

        // 회원 삭제 시도
        mockMvc.perform(post("/api/members/delete/{id}", 1L)
                .param("username", "deletetest")
                .param("password", "deletepassword"))
                .andExpect(status().isOk());
    }
}
