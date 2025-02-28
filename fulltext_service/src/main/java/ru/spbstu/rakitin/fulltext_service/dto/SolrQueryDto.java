package ru.spbstu.rakitin.fulltext_service.dto;

import lombok.Data;
import org.apache.solr.client.solrj.SolrQuery;

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
        private SolrQuery.ORDER order;
    }

}
