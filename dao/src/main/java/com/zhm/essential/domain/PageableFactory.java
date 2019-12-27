package com.zhm.essential.domain;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 制造Pageable的工厂类
 @author gelif
 @since  2015-5-18
 */
@SuppressWarnings({"unchecked", "unused"})
public class PageableFactory {
    private final static int defaultSize = 20;
    private final static String defaultSort = null;

    public static Pageable create(Integer page, Integer size) {
        return new PageRequest(page, null != size && size > 0 ? size : defaultSize);
    }

    public static Pageable create(Integer page, Integer size, int defaultSize) {
        return new PageRequest(page, null != size && size > 0 ? size : defaultSize);
    }

    public static Pageable create(Integer page, Integer size, Sort sort) {
        return new PageRequest(page, null != size && size > 0 ? size : defaultSize, sort);
    }

    public static Pageable create(Integer page, Integer size, int defaultSize, Sort sort) {
        return new PageRequest(page, null != size && size > 0 ? size : defaultSize, sort);
    }

    public static Pageable create(Integer page, Integer size, String sort, String order) {
        return create(page, size, defaultSize, sort, defaultSort, order);
    }

    public static Pageable create(Integer page, Integer size, int defaultSize, String sort, String order) {
        return create(page, size, defaultSize, sort, defaultSort, order);
    }

    public static Pageable create(Integer page, Integer size, String sort, String defaultSort, String order) {
        return create(page, size, defaultSize, sort, defaultSort, order);
    }

    public static Pageable create(Integer page, Integer size, int defaultSize, String sortProperty, String defaultSort, String order) {
        Sort sort = SortFactory.create(sortProperty == null ? defaultSort : sortProperty, order);
        return new PageRequest(page, null != size && size > 0 ? size : defaultSize, sort);
    }

    public static Pageable create(Pageable pageable, String sortPropterty, Sort.Direction direction) {
        Sort sort = SortFactory.create(pageable.getSort(), sortPropterty, direction);
        return create(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

    public static Pageable create(Pageable pageable, String sortPropterty) {
        Sort sort = SortFactory.create(pageable.getSort(), sortPropterty);
        return create(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

    public static Pageable create(Pageable pageable, Sort sort) {
        sort = SortFactory.create(pageable.getSort(), sort);
        return create(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

    public static Pageable create(Pageable pageable, Sort.Order... orders) {
        Sort sort = SortFactory.create(pageable.getSort(), orders);
        return create(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }
}
