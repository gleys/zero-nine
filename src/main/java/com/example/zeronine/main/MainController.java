package com.example.zeronine.main;

import com.example.zeronine.notification.respository.NotificationRepository;
import com.example.zeronine.order.Orders;
import com.example.zeronine.order.respository.OrderRepository;
import com.example.zeronine.user.*;
import com.example.zeronine.user.repository.UserRepository;
import com.example.zeronine.user.security.CurrentUser;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MainController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final NotificationRepository notificationRepository;

    @GetMapping("/")
    public String home(@CurrentUser User user, Model model,
                       @PageableDefault(size = 9, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        if (user != null) {
            model.addAttribute(user);
        }

        long count = notificationRepository.countByUserAndChecked(user, false);
        model.addAttribute("hasNotification", count > 0);

        Page<Orders> orderPage = orderRepository.findAll(pageable);
        model.addAttribute("orderPage", orderPage);

        return "index";
    }

    //인증메일 발송 확인
    @GetMapping("/check-email")
    public String checkEmail(@CurrentUser User user, Model model) {
        model.addAttribute("email", user.getEmail());
        return "user/check-email";
    }

    //인증메일 재전송
    @GetMapping("/resend-confirm-email")
    public String resendConfirmEmail(@CurrentUser User user, Model model) throws MessagingException {
        if (!user.canSendConfirmEmail()) {
            model.addAttribute("error", "인증 이메일은 30분에 한번 전송할 수 있습니다.");
            model.addAttribute("email", user.getEmail());
            return "user/check-email";
        }

        userService.sendConfirmEmail(user, "check-email-token");
        return "redirect:/";
    }

    //비밀번호 찾기
    @GetMapping("/find-password")
    public String findPasswordForm() {
        return "user/find-password";
    }

    @PostMapping("/find-password")
    public String findPassword(@RequestParam String email, Model model, RedirectAttributes attributes) throws MessagingException {
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            model.addAttribute("error", "존재하지 않는 계정 입니다.");
            return "user/find-password";
        }

        if (!user.canSendConfirmEmail()) {
            model.addAttribute("error", "인증 이메일은 30분에 한번 전송할 수 있습니다.");
            return "user/find-password";
        }

        userService.sendConfirmEmail(user, "/find-password");
        attributes.addFlashAttribute("message", "가입 때 사용하신 메일 계정으로 비밀번호 찾기 링크를 보냈습니다.");

        return "redirect:/user/find-password";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/search/orders")
    public String searchKeyword(@PageableDefault(size = 9, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                                @RequestParam String keyword, Model model) {

        Page<Orders> orderPage = orderRepository.findByKeyword(keyword, pageable);
        model.addAttribute("orderPage", orderPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortProperty",
                            pageable.getSort().toString().contains("createdAt") ? "createdAt" : "viewCount");

        return "search";

    }

}
