package com.itm.advice.fileservice.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PageUtils {

    private PageUtils() {}

    public static Pageable pageable(int page, int size, String sort) {
        if (page < 0 || size < 0) throw new IllegalArgumentException(
                "PageUtils::pageable : Page and size must be non-negative");

        Sort pageSort = (sort != null) ? Sort.by(Sort.Order.asc(sort)) : Sort.unsorted();

        return PageRequest.of(page, size, pageSort);
    }

    public static Pageable pageable(int page, int size) {
        return pageable(page, size, null);
    }
}