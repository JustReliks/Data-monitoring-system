package ru.spbstu.rakitin.dto;

import lombok.Data;
import ru.spbstu.rakitin.model.Book;

@Data
public class MdsBookDto {

    private String title;
    private String description;
    private Long libraryId;

    public static MdsBookDto fromBook(final Book book) {
        MdsBookDto dto = new MdsBookDto();
        dto.title = book.getTitle();
        dto.description = book.getDescription();
        dto.libraryId = book.getId();

        return dto;
    }

}
