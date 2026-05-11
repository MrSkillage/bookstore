package com.pageturn.bookstore.book;

import com.pageturn.bookstore.book.dto.BookResponse;
import com.pageturn.bookstore.book.dto.BookSearchCriteria;
import com.pageturn.bookstore.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;

    public Page<BookResponse> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .map(this::toResponse);
    }

    public Page<BookResponse> searchBooks(BookSearchCriteria criteria, Pageable pageable) {
        var spec = BookSpecification.fromCriteria(criteria);
        return bookRepository.findAll(spec, pageable)
                .map(this::toResponse);
    }

    public BookResponse getBookById(UUID id) {
        var book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
        return toResponse(book);
    }

    public BookResponse getBookByIsbn(String isbn) {
        var book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "isbn", isbn));
        return toResponse(book);
    }

    private BookResponse toResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getGenre(),
                book.getDescription(),
                book.getPrice(),
                book.getStockQuantity(),
                book.getPublishedDate()
        );
    }

}
