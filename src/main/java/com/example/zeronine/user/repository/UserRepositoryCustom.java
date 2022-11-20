package com.example.zeronine.user.repository;

import com.example.zeronine.settings.Keyword;
import com.example.zeronine.user.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UserRepositoryCustom {
    Map<String, List<User>> findNotificationsTargetUsers(Set<Keyword> keywords);
}
