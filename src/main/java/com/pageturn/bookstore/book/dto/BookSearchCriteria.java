package com.pageturn.bookstore.book.dto;

import com.pageturn.bookstore.book.Genre;

import java.math.BigDecimal;

public record BookSearchCriteria(
        String title,
        String author,
        Genre genre,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Boolean inStock
) {}
