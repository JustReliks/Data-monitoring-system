package ru.spbstu.rakitin.dto;

import lombok.Data;

@Data
public class PageMessageDto {

    private String content;
    private int page;
    private String filename;
    private String bookPath;

}
