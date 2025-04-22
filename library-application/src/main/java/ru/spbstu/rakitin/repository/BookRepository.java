package ru.spbstu.rakitin.repository;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.spbstu.rakitin.model.Book;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByDescriptionLike(String description);

    List<Book> findByDescriptionLike(String description, Limit limit);
}
