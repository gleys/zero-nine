package com.example.zeronine.user;

import com.example.zeronine.user.form.JoinForm;
import com.example.zeronine.user.repository.UserRepository;
import com.example.zeronine.user.security.CurrentUser;
import com.example.zeronine.user.validator.JoinFormValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final JoinFormValidator joinFormValidator;

    @InitBinder("joinForm")
    public void joinValid(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(joinFormValidator);
    }

    @GetMapping("/join")
    public String joinForm(Model model) {
        model.addAttribute(new JoinForm());
        return "user/join";
    }

    @PostMapping("/join")
    public String submit(@Validated JoinForm joinForm, BindingResult errors) throws MessagingException {
        if (errors.hasErrors()) {
            return "user/join";
        }

        User user = userService.join(joinForm);
        userService.login(user);

        return "redirect:/";
    }

    //인증 확인 메일 토큰값 검증
    @GetMapping("/check-email-token")
    public String emailVerity(@RequestParam String token, @RequestParam String email, Model model) {
        boolean verifyResult = userService.verifyEmail(email, token);

        if (verifyResult == false) {
            model.addAttribute("error", "wrong.info");
        }

        return "user/checked-email";
    }

    @GetMapping("/profile/{username}")
    public String profile(@CurrentUser User user, @PathVariable String username, Model model) {
        User findUser = userRepository.findByName(username)
                .orElseThrow(() -> new IllegalArgumentException(username + "에 해당하는 사용자가 없습니다."));

        model.addAttribute("isOwner", user.equals(findUser));
        model.addAttribute(findUser);
        return "user/profile";
    }
}
