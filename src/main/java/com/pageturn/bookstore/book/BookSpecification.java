package com.pageturn.bookstore.book;

import com.pageturn.bookstore.book.dto.BookSearchCriteria;
import org.springframework.data.jpa.domain.Specification;

public final class BookSpecification {

    private BookSpecification() {
        // Prevents instantiation, only to be used for Utility
    }

    // Specification .where is deprecated, will need to find alternative
    public static Specification<Book> fromCriteria(BookSearchCriteria criteria) {
        return Specification
                .where(titleContains(criteria.title()))
                .and(authorContains(criteria.author()));
    }

    private static Specification<Book> titleContains(String title) {
        return (root, query, cb) -> {
            if (title == null || title.isBlank()) {
                return null; // Ignored by Spring Data (may need a better fix later)
            }
            return cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
        };
    }

    private static Specification<Book> authorContains(String author) {
        return (root, query, cb) -> {
            if (author == null || author.isBlank()) {
                return null;
            }
            return cb.like(cb.lower(root.get("author")), "%" + author.toLowerCase() + "%");
        };
    }

    /*
        Need to create searchable criteria for all Book variables.
    */

}
