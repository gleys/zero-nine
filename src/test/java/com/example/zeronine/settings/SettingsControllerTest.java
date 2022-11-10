package com.example.zeronine.settings;

import com.example.zeronine.user.User;
import com.example.zeronine.user.UserRepository;
import com.example.zeronine.user.UserService;
import com.example.zeronine.user.form.JoinForm;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired UserService userService;
    @Autowired UserRepository userRepository;
    @Autowired PasswordEncoder passwordEncoder;

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
    @DisplayName("프로필 수정 폼")
    @WithUserDetails(value = "glesy", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void editProfileForm() throws Exception {
        mockMvc.perform(get("/settings/profile"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("profileEditForm"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/profile"));
    }


    @Test
    @DisplayName("프로필 수정 - 입력값 정상")
    @WithUserDetails(value = "glesy", setupBefore = TestExecutionEvent.TEST_EXECUTION) //유저 인증정보 추가
    void updateProfile() throws Exception {
        String newBio = "bio 수정";
        mockMvc.perform(post("/settings/profile")
                .with(csrf())
                .param("bio", newBio))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/profile"))
                .andExpect(flash().attributeExists("message"));

        User user = userRepository.findByName("glesy").get();
        assertThat(user.getBio()).isEqualTo(newBio);
    }

    @Test
    @DisplayName("프로필 수정 - 입력값 오류")
    @WithUserDetails(value = "glesy", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void updateProfile_error() throws Exception {

        String newBio = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        mockMvc.perform(post("/settings/profile")
                        .with(csrf())
                        .param("bio", newBio))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/profile"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("profileEditForm"));

        User user = userRepository.findByName("glesy").get();
        assertNull(user.getBio());
    }

    @Test
    @DisplayName("비밀번호 수정 폼")
    @WithUserDetails(value = "glesy", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void changePassword_form() throws Exception{

        mockMvc.perform(get("/settings/password"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("passwordForm"));
    }


    @Test
    @DisplayName("비밀번호 변경 - 성공")
    @WithUserDetails(value = "glesy", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void changePassword() throws Exception{
        String beforePassword = "test12345tt";
        String newPassword = "test!!Rdeqtezcxv23";

        mockMvc.perform(post("/settings/password")
                .with(csrf())
                .param("password", newPassword)
                .param("passwordConfirm", newPassword))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/password"))
                .andExpect(flash().attributeExists("message"));

        User user = userRepository.findByName("glesy").get();
        assertFalse(passwordEncoder.matches(beforePassword, user.getPassword()));
    }

    @Test
    @DisplayName("비빌번호 변경 - 입력값 오류")
    @WithUserDetails(value = "glesy", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void changePassword_error() throws Exception {
        String beforePassword = "test12345tt";
        String password1 = "test12345tt";
        String password2 = "test!!Rdeqtezcxv23";
        String password3 = "test24";

        //변경할 비빌번호가 기존과 동일
        mockMvc.perform(post("/settings/password")
                        .with(csrf())
                        .param("password", password1)
                        .param("passwordConfirm",password1))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/password"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().hasErrors());

        //비밀번호 폼 불일치
        mockMvc.perform(post("/settings/password")
                        .with(csrf())
                        .param("password", password1)
                        .param("passwordConfirm",password2))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/password"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().hasErrors());

        //비밀번호 양식 불충족
        mockMvc.perform(post("/settings/password")
                        .with(csrf())
                        .param("password", password3)
                        .param("passwordConfirm",password3))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/password"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().hasErrors());

        User user = userRepository.findByName("glesy").get();
        assertTrue(passwordEncoder.matches(beforePassword, user.getPassword()));
    }

    @Test
    @DisplayName("키워드 알림 뷰")
    void keywordView() {
    }


    @Test
    @DisplayName("키워드 알림 추가 - 성공")
    void addKeyword() {
        // TODO : 키워드 알림 추가 성공 테스트
    }

    @Test
    @DisplayName("키워드 알림 추가 - 실패")
    void addKeyword_fail() {
        /**
         * TODO : 키워드 알림 추가 실패 테스트
         * case1 : 양식에 맞지 않는 키워드 추가 요청(글자 수, 특수문자)
         * case2 : 존재하는 키워드 중복 추가 요청
         * case3 : 권한없는 유저의 추가 요청
         **/
    }

    @Test
    @DisplayName("키워드 알림 삭제 - 성공")
    void removeKeyword() {
        // TODO : 키워드 알림 삭제 성공 테스트
    }

    @Test
    @DisplayName("키워드 알림 삭제 - 실패")
    void removeKeyword_fail() {
        /**
         * TODO : 키워드 알림 삭제 실패 테스트
         * case1 : 존재하지 않는 키워드 삭제 요청
         * case2 : 유저에게 없는 키워드 삭제 요청
         * case3 : 권한없는 유저의 삭제 요청
         **/
    }





}