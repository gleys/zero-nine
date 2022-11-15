package com.example.zeronine.order;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOrder is a Querydsl query type for Order
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOrder extends EntityPathBase<Order> {

    private static final long serialVersionUID = -1404897461L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOrder order = new QOrder("order1");

    public final BooleanPath closed = createBoolean("closed");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.example.zeronine.item.QItem item;

    public final ListPath<com.example.zeronine.settings.Keyword, com.example.zeronine.settings.QKeyword> keywords = this.<com.example.zeronine.settings.Keyword, com.example.zeronine.settings.QKeyword>createList("keywords", com.example.zeronine.settings.Keyword.class, com.example.zeronine.settings.QKeyword.class, PathInits.DIRECT2);

    public final NumberPath<Integer> numberOfLimit = createNumber("numberOfLimit", Integer.class);

    public final com.example.zeronine.user.QUser owner;

    public final NumberPath<Integer> participantNum = createNumber("participantNum", Integer.class);

    public final StringPath title = createString("title");

    public final SetPath<com.example.zeronine.user.User, com.example.zeronine.user.QUser> users = this.<com.example.zeronine.user.User, com.example.zeronine.user.QUser>createSet("users", com.example.zeronine.user.User.class, com.example.zeronine.user.QUser.class, PathInits.DIRECT2);

    public QOrder(String variable) {
        this(Order.class, forVariable(variable), INITS);
    }

    public QOrder(Path<? extends Order> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOrder(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOrder(PathMetadata metadata, PathInits inits) {
        this(Order.class, metadata, inits);
    }

    public QOrder(Class<? extends Order> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.item = inits.isInitialized("item") ? new com.example.zeronine.item.QItem(forProperty("item"), inits.get("item")) : null;
        this.owner = inits.isInitialized("owner") ? new com.example.zeronine.user.QUser(forProperty("owner")) : null;
    }

}

