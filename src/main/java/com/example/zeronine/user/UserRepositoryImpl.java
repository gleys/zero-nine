package com.example.zeronine.user;

import com.example.zeronine.settings.QKeyword;
import com.example.zeronine.settings.QUserKeyword;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Map<String, List<User>> findNotificationsTargetUsers(List<String> words) {
        QKeyword keyword = QKeyword.keyword;
        QUserKeyword userKeyword = QUserKeyword.userKeyword;
        QUser user = QUser.user;

        Map<String, List<User>> keywordMap = new HashMap<>();

        for (String word : words) {
            keywordMap.put(word, new ArrayList<User>());
        }

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
                .where(keyword.name.in(words)).fetch();

        for (Tuple tuple : fetch) {
            String targetKeyword = tuple.get(0, String.class);
            User targetUser = tuple.get(1, User.class);

            keywordMap.get(targetKeyword).add(targetUser);
        }

//                .map(tuple -> keywordMap.get(tuple.get(1, String.class))
//                                .add(tuple.get(0, User.class)));

        return keywordMap;
    }
}
