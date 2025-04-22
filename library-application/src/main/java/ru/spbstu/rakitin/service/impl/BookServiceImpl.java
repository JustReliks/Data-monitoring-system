package ru.spbstu.rakitin.service.impl;

import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.model.Book;
import ru.spbstu.rakitin.repository.BookRepository;
import ru.spbstu.rakitin.service.BookService;

import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    public Optional<Book> getBook(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    public List<Book> findByDescription(String description, int limit) {
        return bookRepository.findByDescriptionLike(description, Limit.of(limit));
    }
}
