package com.example.zeronine.user;

import com.example.zeronine.settings.Keyword;

import java.util.List;
import java.util.Map;

public interface UserRepositoryCustom {
    Map<String, List<User>> findNotificationsTargetUsers(List<Keyword> kewords);
}
