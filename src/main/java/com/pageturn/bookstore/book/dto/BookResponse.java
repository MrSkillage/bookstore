package com.pageturn.bookstore.book.dto;

import com.pageturn.bookstore.book.Genre;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record BookResponse (
        UUID id,
        String isbn,
        String title,
        String author,
        Genre genre,
        String description,
        BigDecimal price,
        int stockQuantity,
        LocalDate publishedDate
) {}
