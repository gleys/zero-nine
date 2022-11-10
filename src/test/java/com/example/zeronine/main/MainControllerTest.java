package com.example.zeronine.main;

import com.example.zeronine.mail.EmailMessage;
import com.example.zeronine.mail.EmailService;
import com.example.zeronine.user.form.JoinForm;
import com.example.zeronine.user.User;
import com.example.zeronine.user.UserRepository;
import com.example.zeronine.user.UserService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.validation.constraints.Email;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@Transactional
@SpringBootTest(properties = "dev")
@AutoConfigureMockMvc
class MainControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired UserService userService;
    @Autowired UserRepository userRepository;
    @MockBean EmailService mailSender;


    class MockUser extends User {
        private String email;
        private String name;
        private String password;

        @Override
        public String getEmail() {
            return email;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getPassword() {
            return password;
        }

        public MockUser(User user) {
            this.email = user.getEmail();
            this.name = user.getName();
            this.password = user.getPassword();
        }

        @Override
        public boolean canSendConfirmEmail() {
            return true;
        }
    }

    @BeforeEach
    void beforeEach() throws MessagingException {
        JoinForm form = new JoinForm("glesy", "hse6041@naver.com", "test12345tt");
        userService.join(form);
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("계정인증 확인 알람 - 로그인 상태")
    @WithUserDetails(value = "glesy", setupBefore = TestExecutionEvent.TEST_EXECUTION) //유저 인증정보 추가
    void checkEmail_view_correct() throws Exception {
        mockMvc.perform(get("/check-email"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("계정인증 확인 알람 - 비로그인 상태")
    void checkEmail_view_wrong() throws Exception {
        mockMvc.perform(post("/logout")
                .with(csrf()));

        mockMvc.perform(get("/check-email"))
                .andExpect(status().is3xxRedirection());
//                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithUserDetails(value = "glesy", setupBefore = TestExecutionEvent.TEST_EXECUTION) //유저 인증정보 추가
    @DisplayName("인증메일 재전송 - 조건 불충족")
    void resend_confirm_email_wrong() throws Exception {
        mockMvc.perform(get("/resend-confirm-email")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("user/check-email"));
    }

    @Test
    @DisplayName("인증메일 재전송 - 조건 충족")
    void resend_confirm_email_correct() throws Exception {
        MockUser mockUser = new MockUser(userRepository.findByEmail("hse6041@naver.com").get());

        assertThat(mockUser.getEmail()).isEqualTo("hse6041@naver.com");

        userService.login(mockUser);

        mockMvc.perform(get("/resend-confirm-email")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
                .andExpect(authenticated());

        //mailSender.send(SimpleMailMessage) 두번 호출 되었나 확인 (최초 가입시 1번, 재전송 1번)
        verify(mailSender, times(2)).send(any(EmailMessage.class));
    }

    @Test
    @DisplayName("이메일 로그인 성공")
    void login_with_email() throws Exception {
        mockMvc.perform(post("/login")
                .param("username","hse6041@naver.com")
                .param("password", "test12345tt")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("glesy"));
    }

    @Test
    @DisplayName("닉네임 로그인 성공")
    void login_with_name() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username","glesy")
                        .param("password", "test12345tt")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("glesy"));
    }

    @Test
    @DisplayName("로그인 실패")
    void login_fail() throws Exception {
        mockMvc.perform(post("/login")
                        .param("username","adsf")
                        .param("password", "test12345tt")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());

        mockMvc.perform(post("/login")
                        .param("username","glesy")
                        .param("password", "asdf")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("로그아웃")
    void logout() throws Exception {
        mockMvc.perform(post("/logout")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("비밀번호 찾기 - 잘못된 메일양식")
    void findPassword_wrong() throws Exception {
        mockMvc.perform(post("/find-password")
                        .with(csrf())
                        .param("email", "asdfsadf"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/find-password"))
                .andExpect(model().attributeExists("error"));
    }

//    @Test
//    @DisplayName("비밀번호 찾기 - 성공")
//    void findPassword() throws Exception {
//        MockUser mockUser = new MockUser(userRepository.findByEmail("hse6041@naver.com").get());
//
//        mockMvc.perform(post("/find-password")
//                        .with(csrf())
//                        .param("email", "hse6041@naver.com"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(view().name("redirect:/find-password"))
//                .andExpect(model().attributeExists("message"));
//    }
//



}