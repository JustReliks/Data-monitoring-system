package ru.spbstu.rakitin.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.spbstu.rakitin.model.Book;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class BookDto {

    private Long libraryId;
    private String title;
    private String description;
    private String author;
    private String publisher;
    private String publishedDate;
    private double price;

    public Book toBook() {
        Book book = new Book();
        book.setTitle(title);
        book.setDescription(description);
        book.setAuthor(author);
        book.setPublisher(publisher);
        book.setPrice(price);
        book.setId(libraryId);
        OffsetDateTime offsetDateTime = OffsetDateTime.from(DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(publishedDate));
        book.setPublishedDate(new Date(offsetDateTime.toInstant().toEpochMilli()));

        return book;
    }

    public static BookDto fromBook(Book book) {
        BookDto bookDto = new BookDto();
        bookDto.title = book.getTitle();
        bookDto.description = book.getDescription();
        bookDto.author = book.getAuthor();
        bookDto.publisher = book.getPublisher();
        bookDto.publishedDate = book.getPublishedDate().toString();
        bookDto.price = book.getPrice();
        bookDto.libraryId = book.getId();
        return bookDto;
    }

}
