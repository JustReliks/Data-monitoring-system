package ru.spbstu.rakitin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchTimeDto {

    private String searchType;
    private long booksCount;
    private long searchTime;
    private String timestamp;

}
