package com.example.zeronine.user;

import com.example.zeronine.settings.form.ProfileEditForm;
import com.example.zeronine.user.form.JoinForm;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.LocalDateTime;

import java.util.*;

import static javax.persistence.FetchType.*;
import static lombok.AccessLevel.PROTECTED;

@Slf4j
@Getter
@EqualsAndHashCode(of = "id")
@Table(name = "users")
@NoArgsConstructor(access = PROTECTED)
@Entity
public class User {

    @Column(name = "users_id")
    @Id @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String emailCheckToken;

    private boolean emailVerified = Boolean.FALSE;

    private LocalDateTime createdAt;

    private String bio;

    private String url;

    @Lob @Basic(fetch = EAGER)
    private String profileImage;

    private boolean orderCreatedByEmail;

    private boolean orderCreatedByWeb;

    private boolean orderEnrollmentResultByEmail;

    private boolean orderEnrollmentResultByWeb;

    private boolean orderUpdatedByEmail;

    private boolean orderUpdatedByWeb;

    private LocalDateTime emailCheckTokenGeneratedAt;

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    public void changeUsername(String username) {
        this.name = username;
    }

    public void setProfileInfo(ProfileEditForm form) {
        this.bio = form.getBio();
        this.url = form.getUrl();
        this.profileImage = form.getProfileImage();
    }

    public void completeJoin() {
        this.emailVerified = true;
        this.createdAt = LocalDateTime.now();
    }

    public void generateToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
    }

    public boolean canSendConfirmEmail() {
        log.info("token generated time = {}", this.emailCheckTokenGeneratedAt);
        log.info("is before 30 minutes? {}", this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusMinutes(30)));
        return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusMinutes(30));
    }

    public boolean isValidToken(String token) {
        return this.emailCheckToken.equals(token);
    }

    public static User of(JoinForm form) {
        User user = new User();
        user.name = form.getUsername();
        user.email = form.getEmail();
        user.password = form.getPassword();

        user.emailCheckTokenGeneratedAt = LocalDateTime.now();

        user.orderCreatedByWeb = true;
        user.orderEnrollmentResultByWeb = true;
        user.orderUpdatedByWeb = true;

        return user;
    }


}
