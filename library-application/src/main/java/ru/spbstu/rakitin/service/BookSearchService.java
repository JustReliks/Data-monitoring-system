package ru.spbstu.rakitin.service;

import ru.spbstu.rakitin.dto.BookSearchRequestDto;
import ru.spbstu.rakitin.model.Book;

import java.util.List;

public interface BookSearchService {

    List<Book> findByDescriptionMds(BookSearchRequestDto searchRequestDto);

    List<Book> findByDescriptionDatabase(BookSearchRequestDto searchRequestDto);
}
