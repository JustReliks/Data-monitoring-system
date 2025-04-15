package ru.spbstu.rakitin.dto;

import lombok.Data;

@Data
public class BookDto {

    private String title;
    private String description;
    private String author;
    private String publisher;
    private String publishedDate;
    private double price;

}
