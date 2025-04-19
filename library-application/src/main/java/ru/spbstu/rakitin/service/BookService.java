package ru.spbstu.rakitin.service;


import ru.spbstu.rakitin.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {

    Book saveBook(Book book);

    Optional<Book> getBook(Long id);

    List<Book> findByDescription(String description);
}
