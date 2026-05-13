package com.pageturn.bookstore.book;

import com.pageturn.bookstore.book.dto.BookSearchCriteria;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public final class BookSpecification {

    private BookSpecification() {
        // Prevents instantiation, only to be used for Utility
    }

    // Specification .where is deprecated, will need to find alternative
    public static Specification<Book> fromCriteria(BookSearchCriteria criteria) {
        return Specification
                .where(titleContains(criteria.title()))
                .and(authorContains(criteria.author()))
                .and(genreEquals(criteria.genre()))
                .and(priceGreaterThanOrEqual(criteria.minPrice()))
                .and(priceLessThanOrEqual(criteria.maxPrice()))
                .and(inStock(criteria.inStock()));
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

    private static Specification<Book> genreEquals(Genre genre) {
        return (root, query, cbs) -> {
            if (genre == null) return null;
            return cbs.equal(root.get("genre"), genre);
        };
    }

    private static Specification<Book> priceGreaterThanOrEqual(BigDecimal minPrice) {
        return (root, query, cbs) -> {
            if (minPrice == null) return null;
            return cbs.greaterThanOrEqualTo(root.get("price"), minPrice);
        };
    }

    private static Specification<Book> priceLessThanOrEqual(BigDecimal maxPrice) {
        return (root, query, cbs) -> {
          if (maxPrice == null) return null;
          return cbs.lessThanOrEqualTo(root.get("price"), maxPrice);
        };
    }

    private static Specification<Book> inStock(Boolean inStock) {
        return (root, query, cbs) -> {
          if (inStock == null || !inStock) return null;
          return cbs.greaterThan(root.get("stockQuantity"), 0);
        };
    }

}
