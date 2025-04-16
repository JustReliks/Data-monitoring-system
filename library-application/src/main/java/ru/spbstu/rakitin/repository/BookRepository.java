package ru.spbstu.rakitin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.spbstu.rakitin.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
}
