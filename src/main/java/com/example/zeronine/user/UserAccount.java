package com.example.zeronine.user;

import lombok.Getter;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

//시큐리티가 사용하는 user와 도메인 user 사이의 어댑터 역할, 로그인 시 UserAccount를 principal 객체로 사용
@Getter
public class UserAccount extends User {
    private com.example.zeronine.user.User user;

    public UserAccount(com.example.zeronine.user.User user) {
        super(user.getName(), user.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        this.user = user;
    }
}
