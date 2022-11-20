package com.example.zeronine.user.repository;

import com.example.zeronine.settings.Keyword;
import com.example.zeronine.settings.QKeyword;
import com.example.zeronine.settings.QUserKeyword;
import com.example.zeronine.user.QUser;
import com.example.zeronine.user.User;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Map<String, List<User>> findNotificationsTargetUsers(Set<Keyword> keywords) {
        QKeyword keyword = QKeyword.keyword;
        QUserKeyword userKeyword = QUserKeyword.userKeyword;
        QUser user = QUser.user;

        Map<String, List<User>> keywordMap = new HashMap<>();

        keywords.stream().forEach(o -> keywordMap.put(o.getName(), new ArrayList<User>()));

//        for (String word : keywords.stream().map(o -> o.getName()).collect(Collectors.toList())) {
//            keywordMap.put(word, new ArrayList<User>());
//        }

//        queryFactory.select(user, keyword.name)
//                .from(keyword).where(keyword.name.in(words))
//                .innerJoin(userKeyword.keyword, keyword)
//                .innerJoin(userKeyword.user, user)
//                .groupBy(keyword.name)
//                .fetch().stream()
//                .map(tuple -> keywordMap.get(tuple.get(1, String.class))
//                        .add(tuple.get(0, User.class)));

        //query factory??
        List<Tuple> fetch = queryFactory.select(keyword.name, user).from(userKeyword)
                .innerJoin(userKeyword.keyword, keyword)
                .innerJoin(userKeyword.user, user)
                .where(keyword.name.in(keywordMap.keySet())).fetch();

        fetch.forEach(tuple -> {
            String targetKeyword = tuple.get(0, String.class);
            User targetUser = tuple.get(1, User.class);

            keywordMap.get(targetKeyword).add(targetUser);
        });

//        for (Tuple tuple : fetch) {
//            String targetKeyword = tuple.get(0, String.class);
//            User targetUser = tuple.get(1, User.class);
//
//            keywordMap.get(targetKeyword).add(targetUser);
//        }

//                .map(tuple -> keywordMap.get(tuple.get(1, String.class))
//                                .add(tuple.get(0, User.class)));

        return keywordMap;
    }
}
