package ru.spbstu.rakitin.fulltext_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SchemaField {

    private String fieldName;
    private FieldType fieldType;
    private FieldType subType;

    public enum FieldType {
        DOUBLE("pdouble"), LONG("plong"), STRING("string"), TEXT("text_general"), DATE("pdate"), ARRAY("array");

        private final String solrType;

        FieldType(String solrTypeName) {
            this.solrType = solrTypeName;
        }

        public String getSolrType() {
            return solrType;
        }

        public String getSolrTypeArray() {
            return getSolrType() + "s";
        }
    }

}
