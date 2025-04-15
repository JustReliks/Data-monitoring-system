package ru.spbstu.rakitin.dto;

import lombok.Data;
import ru.spbstu.rakitin.dto.fulltext.SolrQueryDto;

@Data
public class BookSearchRequestDto {

    private String description;


    public static SolrQueryDto mapToSolrQueryDto(BookSearchRequestDto dto) {
        SolrQueryDto solrQueryDto = new SolrQueryDto();
        solrQueryDto.setQuery("description:" + dto.getDescription());

        return solrQueryDto;
    }
}
