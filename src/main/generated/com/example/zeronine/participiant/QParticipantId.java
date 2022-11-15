package com.example.zeronine.participiant;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QParticipantId is a Querydsl query type for ParticipantId
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QParticipantId extends BeanPath<ParticipantId> {

    private static final long serialVersionUID = 1120891733L;

    public static final QParticipantId participantId = new QParticipantId("participantId");

    public final NumberPath<Long> orderId = createNumber("orderId", Long.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QParticipantId(String variable) {
        super(ParticipantId.class, forVariable(variable));
    }

    public QParticipantId(Path<? extends ParticipantId> path) {
        super(path.getType(), path.getMetadata());
    }

    public QParticipantId(PathMetadata metadata) {
        super(ParticipantId.class, metadata);
    }

}

