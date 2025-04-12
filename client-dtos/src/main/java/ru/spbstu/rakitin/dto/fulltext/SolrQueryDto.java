package ru.spbstu.rakitin.dto.fulltext;

import lombok.Data;

import java.util.List;

@Data
public class SolrQueryDto {

    private String query;
    private List<SolrSort> sort;
    private List<String> filters;
    private List<String> reqFields;

    @Data
    public static class SolrSort {
        private String field;
        private SortOrder order;
    }

    public enum SortOrder {
        asc, desc
    }

}
