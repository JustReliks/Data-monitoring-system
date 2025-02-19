package ru.spbstu.rakitin.commonstarter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterExpressionDto {

    private String expression;
    private boolean negate;
    private List<ExpressionConnection> connections;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExpressionConnection {
        private FilterExpressionDto expression;
        private ConnectionType connectionType;
    }

    public enum ConnectionType {
        OR, AND
    }

}
