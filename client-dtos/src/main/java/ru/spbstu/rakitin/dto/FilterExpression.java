package ru.spbstu.rakitin.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterExpression {

    private String expression;
    private boolean negate;
    private List<ExpressionConnection> connections;

    @JsonIgnore
    public List<String> getAllExpressions() {
        List<String> res = new ArrayList<>();
        if (connections != null && !connections.isEmpty()) {
            res.addAll(connections.stream().flatMap(expressionConnection -> expressionConnection.getExpressions().stream())
                    .toList());
        }
        res.add(expression);
        return res;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExpressionConnection {
        private FilterExpression expression;
        private ConnectionType connectionType;

        public List<String> getExpressions() {
            return expression.getAllExpressions();
        }
    }

    public enum ConnectionType {
        OR, AND
    }

}
