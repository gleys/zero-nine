package com.example.zeronine.settings;

import com.example.zeronine.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserKeywordRepository extends JpaRepository<UserKeyword, Long> {
    List<UserKeyword> findByUser(User user);
    void deleteByUserAndKeyword(User user, Keyword keyword);
}
