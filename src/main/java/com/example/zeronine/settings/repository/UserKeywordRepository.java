package com.example.zeronine.settings.repository;

import com.example.zeronine.settings.Keyword;
import com.example.zeronine.settings.UserKeyword;
import com.example.zeronine.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserKeywordRepository extends JpaRepository<UserKeyword, Long> {
    List<UserKeyword> findByUser(User user);

    Optional<UserKeyword> findByUserAndKeyword(User user, Keyword keyword);
    void deleteByUserAndKeyword(User user, Keyword keyword);
}
