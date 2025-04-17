package ru.spbstu.rakitin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookSellMetricDto {

    private String bookName;
    private int sells;

}
