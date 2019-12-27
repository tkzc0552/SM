package com.zhm.essential.domain;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import java.util.Collections;
import java.util.List;

/**
 制造Sort的工厂类
 @author gelif
 @since  2015-5-18
 */
@SuppressWarnings({"unchecked", "unused"})
public class SortFactory {
    private final static Direction defaultDirection = Direction.DESC;

    public static Sort create(String sortProperty, Direction direction) {
        if(sortProperty == null) {
            return null;
        }
        return new Sort(direction, sortProperty);
    }

    public static Sort create(String sortProperty, String order) {
        if(sortProperty == null) {
            return null;
        }
        Direction direction = (StringUtils.isNotBlank(order) && Direction.DESC.name().equalsIgnoreCase(order)) ? Direction.DESC : Direction.ASC;
        return new Sort(direction, sortProperty);
    }

    public static Sort create(Sort sort, String sortPropterty, Direction direction) {
        List<Order> orderList;
        if(sort != null) {
            orderList = Lists.newArrayList(sort.iterator());
        } else {
            orderList = Lists.newArrayList();
        }
        if(sortPropterty != null) {
            orderList.add(new Order(direction != null ? direction : defaultDirection, sortPropterty));
        }
        return CollectionUtils.isEmpty(orderList) ? null : new Sort(orderList);
    }

    public static Sort create(Sort sort, String sortPropterty) {
        return create(sort, sortPropterty, defaultDirection);
    }


    public static Sort create(Sort sort, Order... orders) {
        List<Order> orderList;
        if(sort != null) {
            orderList = Lists.newArrayList(sort.iterator());
        } else {
            orderList = Lists.newArrayList();
        }
        if(orders != null) {
            Collections.addAll(orderList, orders);
        }
        return CollectionUtils.isEmpty(orderList) ? null : new Sort(orderList);
    }

    public static Sort create(Sort... sorts) {
        if(ArrayUtils.isEmpty(sorts)) {
            return null;
        }
        List<Order> orderList = Lists.newArrayList();
        for(Sort sort : sorts) {
            orderList.addAll(Lists.newArrayList(sort.iterator()));
        }
        return new Sort(orderList);
    }
}
