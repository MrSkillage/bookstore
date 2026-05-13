package com.pageturn.bookstore.admin;

import com.pageturn.bookstore.admin.dto.CreateBookRequest;
import com.pageturn.bookstore.book.Book;
import com.pageturn.bookstore.book.BookRepository;
import com.pageturn.bookstore.book.dto.BookResponse;
import com.pageturn.bookstore.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminBookService {

    private final BookRepository bookRepository;

    @Transactional
    public BookResponse createBook(CreateBookRequest request) {
        if (bookRepository.existsByIsbn(request.isbn())) throw new IllegalArgumentException("A book with ISBN: " + request.isbn() + " already exists.");

        var book = Book.builder()
                .isbn(request.isbn())
                .title(request.title())
                .author(request.author())
                .genre(request.genre())
                .description(request.description())
                .price(request.price())
                .stockQuantity(request.stockQuantity())
                .build();

        bookRepository.save(book);
        return toResponse(book);
    }

    public void deleteBook(UUID id) {
        var book = findBookOrThrow(id);
        bookRepository.delete(book);
    }

    private Book findBookOrThrow(UUID id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
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