package com.example.zeronine.order;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;


import static com.querydsl.jpa.JPAExpressions.selectFrom;

public class OrderRepositoryImpl extends QuerydslRepositorySupport implements CustomOrderRepository{

    public OrderRepositoryImpl() {
        super(Order.class);
    }

    @Override
    public Page<Order> findAllByPaging(Pageable pageable) {
        QOrder order = QOrder.order;

        JPQLQuery<Order> query = selectFrom(order)
                .orderBy(order.createdAt.desc());

        JPQLQuery<Order> orderJPQLQuery = getQuerydsl().applyPagination(pageable, query);
        QueryResults<Order> fetchResults = orderJPQLQuery.fetchResults();

        return new PageImpl<>(fetchResults.getResults(), pageable, fetchResults.getTotal());
    }
}
