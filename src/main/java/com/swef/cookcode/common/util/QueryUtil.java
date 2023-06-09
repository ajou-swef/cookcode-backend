package com.swef.cookcode.common.util;

import static java.util.Objects.nonNull;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Sort;

@SuppressWarnings({"rawtypes", "unchecked"})
public class QueryUtil {

    public static OrderSpecifier[] getOrderSpecifiers(Sort sort, List<Expression> popular, Expression recent) {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();
        if (nonNull(sort.getOrderFor("popular"))) {
            popular.forEach(expression -> orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, expression)));
        }
        orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, recent));
        return orderSpecifiers.toArray(new OrderSpecifier[0]);
    }
}
