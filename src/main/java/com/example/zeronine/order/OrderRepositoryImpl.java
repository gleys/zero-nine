package com.example.zeronine.order;

import com.example.zeronine.settings.QKeyword;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;


import static com.querydsl.jpa.JPAExpressions.selectFrom;

public class OrderRepositoryImpl extends QuerydslRepositorySupport implements CustomOrderRepository{

    public OrderRepositoryImpl() {
        super(Orders.class);
    }

    @Override
    public Page<Orders> findAllByPaging(Pageable pageable) {
        QOrders order = QOrders.orders;

        JPQLQuery<Orders> query = selectFrom(order)
                .orderBy(order.createdAt.desc());

        JPQLQuery<Orders> orderJPQLQuery = getQuerydsl().applyPagination(pageable, query);
        QueryResults<Orders> fetchResults = orderJPQLQuery.fetchResults();

        return new PageImpl<>(fetchResults.getResults(), pageable, fetchResults.getTotal());
    }

    @Override
    public Page<Orders> findByKeyword(String word, Pageable pageable) {
        QOrders order = QOrders.orders;
        JPQLQuery<Orders> query = from(order).where(order.keywords.any().name.containsIgnoreCase(word))
                                            .leftJoin(order.keywords, QKeyword.keyword).fetchJoin()
                                            .distinct();

        JPQLQuery<Orders> pageableQuery = getQuerydsl().applyPagination(pageable, query);
        QueryResults<Orders> fetchResults = pageableQuery.fetchResults();

        return new PageImpl<>(fetchResults.getResults(), pageable, fetchResults.getTotal());
    }
}
