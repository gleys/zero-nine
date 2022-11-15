package com.example.zeronine.user;

import java.util.List;
import java.util.Map;

public interface UserRepositoryCustom {
    Map<String, List<User>> findNotificationsTargetUsers(List<String> words);
}
