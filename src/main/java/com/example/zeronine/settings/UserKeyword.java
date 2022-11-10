package com.example.zeronine.settings;

import com.example.zeronine.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "users_keywords")
public class UserKeyword {

    @Id @GeneratedValue
    @Column(name = "users_keywords_id")
    private Long id;

    // fk constraint - cascade 적용 (user가 삭제되면 userkeywords 중간테이블 필요없음)
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_users_to_users_keywords"))
    private User user;

    // fk constraint - cascade 적용 (keyword가 삭제되면 userkeywords 중간테이블 필요없음)
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "keyword_id", foreignKey = @ForeignKey(name = "fk_keywords_to_users_keywords"))
    private Keyword keyword;

    public static UserKeyword of(Keyword keyword, User user) {
        UserKeyword userKeyword = new UserKeyword();
        userKeyword.user = user;
        userKeyword.keyword = keyword;

        return userKeyword;
    }
}
