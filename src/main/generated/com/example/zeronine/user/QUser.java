package com.example.zeronine.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = 394133035L;

    public static final QUser user = new QUser("user");

    public final StringPath bio = createString("bio");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final StringPath emailCheckToken = createString("emailCheckToken");

    public final DateTimePath<java.time.LocalDateTime> emailCheckTokenGeneratedAt = createDateTime("emailCheckTokenGeneratedAt", java.time.LocalDateTime.class);

    public final BooleanPath emailVerified = createBoolean("emailVerified");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final BooleanPath orderCreatedByEmail = createBoolean("orderCreatedByEmail");

    public final BooleanPath orderCreatedByWeb = createBoolean("orderCreatedByWeb");

    public final BooleanPath orderEnrollmentResultByEmail = createBoolean("orderEnrollmentResultByEmail");

    public final BooleanPath orderEnrollmentResultByWeb = createBoolean("orderEnrollmentResultByWeb");

    public final BooleanPath orderUpdatedByEmail = createBoolean("orderUpdatedByEmail");

    public final BooleanPath orderUpdatedByWeb = createBoolean("orderUpdatedByWeb");

    public final StringPath password = createString("password");

    public final StringPath profileImage = createString("profileImage");

    public final StringPath url = createString("url");

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

