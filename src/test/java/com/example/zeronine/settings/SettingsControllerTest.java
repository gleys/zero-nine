package com.example.zeronine.settings;

import com.example.zeronine.settings.form.KeywordsForm;
import com.example.zeronine.settings.repository.UserKeywordRepository;
import com.example.zeronine.user.repository.KeywordRepository;
import com.example.zeronine.user.User;
import com.example.zeronine.user.repository.UserRepository;
import com.example.zeronine.user.UserService;
import com.example.zeronine.user.form.JoinForm;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
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
    @Autowired ObjectMapper objectMapper;
    @Autowired KeywordRepository keywordRepository;
    @Autowired
    UserKeywordRepository userKeywordRepository;

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
    @DisplayName("????????? ?????? ???")
    @WithUserDetails(value = "glesy", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void editProfileForm() throws Exception {
        mockMvc.perform(get("/settings/profile"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("profileEditForm"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/profile"));
    }


    @Test
    @DisplayName("????????? ?????? - ????????? ??????")
    @WithUserDetails(value = "glesy", setupBefore = TestExecutionEvent.TEST_EXECUTION) //?????? ???????????? ??????
    void updateProfile() throws Exception {
        String newBio = "bio ??????";
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
    @DisplayName("????????? ?????? - ????????? ??????")
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
    @DisplayName("???????????? ?????? ???")
    @WithUserDetails(value = "glesy", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void changePassword_form() throws Exception{

        mockMvc.perform(get("/settings/password"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("passwordForm"));
    }


    @Test
    @DisplayName("???????????? ?????? - ??????")
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
    @DisplayName("???????????? ?????? - ????????? ??????")
    @WithUserDetails(value = "glesy", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void changePassword_error() throws Exception {
        String beforePassword = "test12345tt";
        String password1 = "test12345tt";
        String password2 = "test!!Rdeqtezcxv23";
        String password3 = "test24";

        //????????? ??????????????? ????????? ??????
        mockMvc.perform(post("/settings/password")
                        .with(csrf())
                        .param("password", password1)
                        .param("passwordConfirm",password1))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/password"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().hasErrors());

        //???????????? ??? ?????????
        mockMvc.perform(post("/settings/password")
                        .with(csrf())
                        .param("password", password1)
                        .param("passwordConfirm",password2))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/password"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(model().hasErrors());

        //???????????? ?????? ?????????
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
    @DisplayName("????????? ?????? ???")
    @WithUserDetails(value = "glesy", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void keywordView() throws Exception {
        mockMvc.perform(get("/settings/keywords"))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/keywords"))
                .andExpect(model().attributeExists("keywords"))
                .andExpect(model().attributeExists("whitelist"));
    }

    @Test
    @DisplayName("????????? ?????? ?????? - ??????")
    @WithUserDetails(value = "glesy", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void addKeyword() throws Exception {
        User user = userRepository.findByName("glesy").get();
        String test = objectMapper.writeValueAsString(new KeywordsForm("test"));

        // TODO : ????????? ?????? ?????? ?????? ?????????
        mockMvc.perform(post("/settings/keywords/add")
                .with(csrf())
                .content(test)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Keyword keyword = keywordRepository.findByName("test").get();
        assertThat(keyword.getName()).isEqualTo("test");

        UserKeyword byKeyword = userKeywordRepository.findByUserAndKeyword(user, keyword).get();
        assertThat(byKeyword.getUser()).isEqualTo(user);

    }

    @Test
    @DisplayName("????????? ?????? ?????? - ??????")
    @WithUserDetails(value = "glesy", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void addKeyword_fail() throws Exception {
        /**
         * TODO : ????????? ?????? ?????? ?????? ?????????
         * case1 : ????????? ?????? ?????? ????????? ?????? ??????(?????? ???, ????????????)
         * case2 : ???????????? ????????? ?????? ?????? ??????
         * case3 : ???????????? ????????? ?????? ??????
         **/
        String test1 = objectMapper.writeValueAsString(new KeywordsForm("testtesttesttesttesttesttesttesttest" +
                                                                        "testtesttesttesttesttesttesttesttest"));

        String test2 = objectMapper.writeValueAsString(new KeywordsForm("!@#$%^&&*"));

        mockMvc.perform(post("/settings/keywords/add")
                        .with(csrf())
                        .content(test2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(post("/settings/keywords/add")
                        .with(csrf())
                        .content(test1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());



        List<Keyword> all = keywordRepository.findAll();
        assertThat(all.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("????????? ?????? ?????? - ??????")
    void removeKeyword() {
        // TODO : ????????? ?????? ?????? ?????? ?????????
    }

    @Test
    @DisplayName("????????? ?????? ?????? - ??????")
    void removeKeyword_fail() {
        /**
         * TODO : ????????? ?????? ?????? ?????? ?????????
         * case1 : ???????????? ?????? ????????? ?????? ??????
         * case2 : ???????????? ?????? ????????? ?????? ??????
         * case3 : ???????????? ????????? ?????? ??????
         **/
    }





}