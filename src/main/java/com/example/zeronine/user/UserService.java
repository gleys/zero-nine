package com.example.zeronine.user;

import com.example.zeronine.config.AppProperties;
import com.example.zeronine.mail.EmailMessage;
import com.example.zeronine.mail.EmailService;
import com.example.zeronine.settings.Keyword;
import com.example.zeronine.settings.UserKeyword;
import com.example.zeronine.settings.repository.UserKeywordRepository;
import com.example.zeronine.settings.form.NotificationsForm;
import com.example.zeronine.settings.form.ProfileEditForm;
import com.example.zeronine.user.form.JoinForm;
import com.example.zeronine.user.repository.KeywordRepository;
import com.example.zeronine.user.repository.UserRepository;
import com.example.zeronine.user.security.UserAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;

import javax.mail.MessagingException;

import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final KeywordRepository keywordRepository;
    private final UserKeywordRepository userKeywordRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;

    public User join(JoinForm form) throws MessagingException {
        form.setPassword(passwordEncoder.encode(form.getPassword()));
        User user = User.of(form);
        user.generateToken();
        userRepository.save(user);
        sendConfirmEmail(user, "/check-email-token");

        return user;
    }

    //???????????? ?????? ?????? ??????
    public void sendConfirmEmail(User user, String link) throws MessagingException {
        //TODO : ?????? ???????????? ??????, ???????????? ??? ?????? ?????? ?????? ??? db?????? ?????? ??????
        //       (db ??????????????? emailVerified ??? ?????? ???????????? ?????? ???????????? ?????? ???????????????)
        String sendLink = link + "?token=" + user.getEmailCheckToken() + "&email=" + user.getEmail();

        Context context = new Context();
        context.setVariable("link", sendLink);
        context.setVariable("username", user.getName());
        context.setVariable("linkName", "????????? ????????????");
        context.setVariable("message", "???????????? ???????????? ??????????????? ????????? ???????????????.");
        context.setVariable("host", appProperties.getHost());
        log.info("host : {}", appProperties.getHost());

        String process = templateEngine.process("mail/simple-link", context);

        EmailMessage message = EmailMessage.builder()
                .to(user.getEmail())
                .subject("???????????? ???????????? ??????")
                .message(sendLink)
                .build();

        emailService.send(message);

    }

    public boolean verifyEmail(String email, String token) {
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) return false;

        boolean verifyResult = user.isValidToken(token);

        if (verifyResult) {
            user.completeJoin();
            this.login(user); // ????????? ???????????? ?????? ????????? ??????
        }

        return verifyResult;
    }

    public void login(User user) {
        //???????????? ??????????????? ????????? ????????? authenticationManager ??? ????????? ?????? ??????
        //authenticationManager ?????????(PreAuthenticatedAuthenticationProvider???)?????? ????????? ???????????? ??????
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(user),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);
    }

    public void editProfile(User user, ProfileEditForm form) {
        user.setProfileInfo(form);
        // ?????? detach ????????? principal(user ?????????) ????????? merge
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String emailOrName) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(emailOrName)
                .orElseGet(() -> userRepository.findByName(emailOrName)
                        .orElseThrow(() -> new UsernameNotFoundException(emailOrName)));

        return new UserAccount(user);
    }

    public boolean changePassword(User user, String newPassword) {
        if(passwordEncoder.matches(newPassword, user.getPassword())) {
            return false;
        }

        user.changePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    public void setNotifications(NotificationsForm form, User user) {
        modelMapper.map(form, user);
        userRepository.save(user);
    }

    public boolean changeUsername(User user, String username) {
        user.changeUsername(username);
        userRepository.save(user);
        login(user);
        return true;
    }

    public void addKeyword(User user, String name) {
        Optional<User> findUser = userRepository.findByName(user.getName());
        findUser.ifPresent(o -> {
            Keyword keyword = keywordRepository.findByName(name).orElseGet(() -> {
                Keyword newKeyword = Keyword.of(name);
                keywordRepository.save(newKeyword);
                return newKeyword;
            });
            userKeywordRepository.save(UserKeyword.of(keyword, user));
        });
    }

    public List<String> getKeywords(User user) {
        User findUser = userRepository.findByName(user.getName()).orElseThrow();
        return userKeywordRepository.findByUser(findUser).stream().
                map(o -> o.getKeyword().getName())
                .collect(Collectors.toList());
    };

    public void removeKeyword(User user, String name) {
        User findUser = userRepository.findByName(user.getName()).orElseThrow();
        userRepository.delete(findUser);
        Keyword keyword = keywordRepository.findByName(name).orElseThrow();
        userKeywordRepository.deleteByUserAndKeyword(findUser, keyword);
    }
}
