package com.pageturn.bookstore.admin.dto;

import com.pageturn.bookstore.book.Genre;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateBookRequest(
        @NotBlank(message = "ISBN is required")
        @Pattern(regexp = "^978-\\d{1,5}-\\d{1,7}-\\d$", message = "ISBN must be in valid ISBN-13 format (e.g. 978-0-13-468599-1)")
        String isbn,

        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title must not exceed 255 characters")
        String title,

        @NotBlank(message = "Author is required")
        @Size(max = 75, message = "Author must not exceed 75 characters")
        String author,

        @NotNull(message = "Genre is required")
        Genre genre,
        
        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than zero")
        @Digits(integer = 8, fraction = 2, message = "Price must have at most 2 decimal places")
        BigDecimal price,

        @Min(value = 0, message = "Stock quantity cannot be negative")
        int stockQuantity,

        LocalDate publishedDate
) {}