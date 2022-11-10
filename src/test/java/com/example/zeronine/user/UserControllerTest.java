package com.example.zeronine.user;

import com.example.zeronine.mail.EmailMessage;
import com.example.zeronine.mail.EmailService;
import com.example.zeronine.user.form.JoinForm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private UserService userService;
    @MockBean EmailService mailSender;

    @Test
    @DisplayName("회원 가입 화면")
    void joinForm() throws Exception {
        mockMvc.perform(get("/join"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/join"))
                .andExpect(model().attributeExists("joinForm"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("회원 가입 처리 - 입력 오류")
    void joinSubmit_wrong_input() throws Exception {
        mockMvc.perform(post("/join")
                        .param("username", "gleys##")
                        .param("email", "")
                        .param("password", "23f")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("user/join"))
                .andExpect(model().attributeExists("joinForm"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("회원 가입 처리 - 입력 정상")
    void joinSubmit_correct_input() throws Exception {
        mockMvc.perform(post("/join")
                        .param("username", "gleys")
                        .param("email", "hse6041@gmail.com")
                        .param("password", "afgafdg1234")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
                .andExpect(authenticated());

        User user = userRepository.findByEmail("hse6041@gmail.com").get();
        assertThat(user).isNotNull();
        //비밀번호 암호화 적용
        assertThat(userRepository.existsByEmail("hse6041@gmail.com")).isNotEqualTo("afgafdg1234");

        //mailSender.send(SimpleMailMessage) 호출 되었나 확인
        then(mailSender).should().send(any(EmailMessage.class));
    }

    //스프링 시큐리티를 사용하는 mockMvc 에는 시큐리티 지원 기능 추가됨
    @Test
    @DisplayName("인증 메일 확인 - 입력값 오류")
    void checkEmailToken_with_wrong_input() throws Exception {
        mockMvc.perform(get("/check-email-token")
                .param("token", "adsf")
                .param("email", "hse6041@gmailcom"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("user/checked-email"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("인증 메일 확인 - 입력값 정상")
    void checkEmailToken_with_correct_input() throws Exception {
        JoinForm form = new JoinForm("gleys", "hse6041@gmail.com", "qewrty134");
        User user = User.of(form);
        user.generateToken();

        userRepository.save(user);

        mockMvc.perform(get("/check-email-token")
                        .param("token", user.getEmailCheckToken())
                        .param("email", user.getEmail()))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(view().name("user/checked-email"))
                .andExpect(authenticated().withUsername("gleys"));
    }

}