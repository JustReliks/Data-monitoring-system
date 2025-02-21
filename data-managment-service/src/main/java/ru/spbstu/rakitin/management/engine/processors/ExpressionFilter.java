package ru.spbstu.rakitin.management.engine.processors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.streams.kstream.Predicate;
import ru.spbstu.rakitin.commonstarter.dto.FieldType;
import ru.spbstu.rakitin.commonstarter.dto.FilterExpression;
import ru.spbstu.rakitin.commonstarter.dto.SchemaFieldDto;
import ru.spbstu.rakitin.commonstarter.dto.TaskSchemaDto;
import ru.spbstu.rakitin.commonstarter.utils.MapJson;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ExpressionFilter implements Predicate<String, MapJson>, java.util.function.Predicate<MapJson> {

    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("(.*)(>|<|=|reg)(.*)");
    private static final Pattern VALUE_PATTERN = Pattern.compile("\\$\\{(\\S*)}");

    private final java.util.function.Predicate<MapJson> predicate;

    public ExpressionFilter(TaskSchemaDto taskSchemaDto) {
        if(taskSchemaDto.getFilterExpression() == null) {
            predicate = mapJson -> true;
        } else {
            predicate = buildExpression(taskSchemaDto.getFilterExpression(), taskSchemaDto);
        }
    }

    private java.util.function.Predicate<MapJson> buildExpression(FilterExpression expression, TaskSchemaDto taskSchemaDto) {
        java.util.function.Predicate<MapJson> result = buildExpression(expression.getExpression(), taskSchemaDto, expression.isNegate());
        if (expression.getConnections() != null && !expression.getConnections().isEmpty()) {
            for (FilterExpression.ExpressionConnection connection : expression.getConnections()) {
                if (connection.getConnectionType() == FilterExpression.ConnectionType.OR) {
                    result = result.or(buildExpression(connection.getExpression(), taskSchemaDto));
                } else {
                    result = result.and(buildExpression(connection.getExpression(), taskSchemaDto));
                }
            }
        }
        return result;
    }

    private java.util.function.Predicate<MapJson> buildExpression(String expression, TaskSchemaDto taskSchemaDto, boolean negate) {
        log.info("buildExpression - {}({})", negate ? "!" : "", expression);
        java.util.function.Predicate<MapJson> res = getExpressionPredicate(expression, taskSchemaDto);
        if (negate) {
            res = res.negate();
        }
        return res;
    }

    private java.util.function.Predicate<MapJson> getExpressionPredicate(String expression, TaskSchemaDto taskSchemaDto) {
        return mapJson -> {
            log.debug("buildExpressionLambda - {}", expression);
            Matcher matcher = EXPRESSION_PATTERN.matcher(expression);
            matcher.matches();
            Pair<Object, FieldType> leftValue = getValue(matcher.group(1).trim(), taskSchemaDto, mapJson);
            Pair<Object, FieldType> rightValue = getValue(matcher.group(3).trim(), taskSchemaDto, mapJson);
            if (rightValue.getRight() == null) {
                if (leftValue.getRight().isValueCompatible(rightValue.getKey().toString(), null)) {
                    rightValue = Pair.of(rightValue.getKey(), leftValue.getRight());
                }
            }
            String operator = matcher.group(2).trim();
            FieldType fieldType = leftValue.getValue();
            if (fieldType == FieldType.DATE) {
                Long leftDate = DateTimeFormatter.ISO_INSTANT.parse(leftValue.getKey().toString()).getLong(ChronoField.INSTANT_SECONDS);
                Long rightDate = DateTimeFormatter.ISO_INSTANT.parse(leftValue.getKey().toString()).getLong(ChronoField.INSTANT_SECONDS);
                leftValue = Pair.of(leftDate, FieldType.LONG);
                rightValue = Pair.of(rightDate, FieldType.LONG);

            }
            log.info("Building predicate {} {} {}", leftValue.getKey(), operator, rightValue.getKey());
            return evaluateExpression(leftValue.getKey(), rightValue.getKey(), operator);
        };
    }

    private boolean evaluateExpression(Object left, Object right, String operator) {
        if (operator.equalsIgnoreCase("reg")) {
            Pattern pattern = Pattern.compile(right.toString());
            return pattern.matcher(left.toString()).matches();
        }
        if (operator.equals(">")) {
            return Double.parseDouble(left.toString()) > Double.parseDouble(right.toString());
        }
        if (operator.equals("<")) {
            return Double.parseDouble(left.toString()) < Double.parseDouble(right.toString());
        }
        if (operator.equals("=")) {
            return left.equals(right);
        }

        throw new IllegalArgumentException(String.format("Unexpected operator: %s", operator));

    }

    private Pair<Object, FieldType> getValue(String valueExpression, TaskSchemaDto taskSchemaDto, MapJson mapJson) {
        Matcher matcher = VALUE_PATTERN.matcher(valueExpression);
        if (matcher.matches()) {
            String value = matcher.group(1);
            SchemaFieldDto schemaFieldDto = taskSchemaDto.getField(value).orElseThrow(() -> new IllegalArgumentException(String.format("Field with name %s not found in schema. Can't build expression filter!", value)));

            return Pair.of(mapJson.get(value), schemaFieldDto.getFieldType());
        }
        return Pair.of(valueExpression, null);
    }

    @Override
    public boolean test(String key, MapJson value) {
        return test(value);
    }

    @Override
    public boolean test(MapJson mapJson) {
        return predicate.test(mapJson);
    }
}