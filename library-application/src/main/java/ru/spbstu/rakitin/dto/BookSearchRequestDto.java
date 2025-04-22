package ru.spbstu.rakitin.dto;

import lombok.Data;
import ru.spbstu.rakitin.dto.fulltext.SolrQueryDto;

@Data
public class BookSearchRequestDto {

    private String description;
    private int limit;


    public static SolrQueryDto mapToSolrQueryDto(BookSearchRequestDto dto) {
        SolrQueryDto solrQueryDto = new SolrQueryDto();
        solrQueryDto.setRows(dto.getLimit());
        solrQueryDto.setQuery("description:" + dto.getDescription());

        return solrQueryDto;
    }
}
