package ru.spbstu.rakitin.management.engine.processors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import ru.spbstu.rakitin.commonstarter.dto.FilterExpressionDto;
import ru.spbstu.rakitin.commonstarter.dto.SchemaFieldDto;
import ru.spbstu.rakitin.commonstarter.dto.TaskSchemaDto;
import ru.spbstu.rakitin.commonstarter.utils.MapJson;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ExpressionFilter implements Predicate<MapJson> {

    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("(.*)(>|<|=|reg)(.*)");
    private static final Pattern VALUE_PATTERN = Pattern.compile("\\$\\{(\\S*)}");

    private final Predicate<MapJson> predicate;

    public ExpressionFilter(FilterExpressionDto expression, TaskSchemaDto taskSchemaDto) {
        predicate = buildExpression(expression, taskSchemaDto);
    }

    private Predicate<MapJson> buildExpression(FilterExpressionDto expression, TaskSchemaDto taskSchemaDto) {
        Predicate<MapJson> result = buildExpression(expression.getExpression(), taskSchemaDto, expression.isNegate());
        for (FilterExpressionDto.ExpressionConnection connection : expression.getConnections()) {
            if (connection.getConnectionType() == FilterExpressionDto.ConnectionType.OR) {
                result = result.or(buildExpression(connection.getExpression(), taskSchemaDto));
            } else {
                result = result.and(buildExpression(connection.getExpression(), taskSchemaDto));
            }
        }

        return result;
    }

    private Predicate<MapJson> buildExpression(String expression, TaskSchemaDto taskSchemaDto, boolean negate) {
        log.debug("buildExpression - {}", expression);
        Predicate<MapJson> res = getExpressionPredicate(expression, taskSchemaDto);
        if (negate) {
            res = res.negate();
        }
        return res;
    }

    private Predicate<MapJson> getExpressionPredicate(String expression, TaskSchemaDto taskSchemaDto) {
        return mapJson -> {
            log.debug("buildExpressionLambda - {}", expression);
            Matcher matcher = EXPRESSION_PATTERN.matcher(expression);
            if (matcher.matches()) {
                Pair<Object, SchemaFieldDto.FieldType> leftValue = getValue(matcher.group(1).trim(), taskSchemaDto, mapJson);
                Pair<Object, SchemaFieldDto.FieldType> rightValue = getValue(matcher.group(3).trim(), taskSchemaDto, mapJson);
                if (rightValue.getRight() == null) {
                    if (leftValue.getRight().isValueCompatible(rightValue.getKey().toString(), null)) {
                        rightValue = Pair.of(rightValue.getKey(), leftValue.getRight());
                    }
                }
                String operator = matcher.group(2).trim();
                SchemaFieldDto.FieldType fieldType = leftValue.getValue();
                if (!leftValue.getRight().isCompatibleWith(rightValue.getValue())) {
                    throw new IllegalArgumentException(String.format("%s field type is not compatible with type %s", rightValue.getValue(), leftValue.getValue()));
                }
                if (fieldType == SchemaFieldDto.FieldType.DATE) {
                    Long leftDate = DateTimeFormatter.ISO_INSTANT.parse(leftValue.getKey().toString()).getLong(ChronoField.INSTANT_SECONDS);
                    Long rightDate = DateTimeFormatter.ISO_INSTANT.parse(leftValue.getKey().toString()).getLong(ChronoField.INSTANT_SECONDS);
                    leftValue = Pair.of(leftDate, SchemaFieldDto.FieldType.LONG);
                    rightValue = Pair.of(rightDate, SchemaFieldDto.FieldType.LONG);

                }
                if (!isOperatorCompatibleWithType(operator, leftValue.getValue())) {
                    throw new IllegalArgumentException(String.format("%s field type is not compatible with operator %s", leftValue.getValue(), operator));
                }
                log.info("Building predicate {} {} {}", leftValue.getKey(), operator, rightValue.getKey());
                return evaluateExpression(leftValue.getKey(), rightValue.getKey(), operator);


            }
            throw new IllegalArgumentException(String.format("Expression [%s] is not valid!", expression));

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

    private boolean isOperatorCompatibleWithType(String operator, SchemaFieldDto.FieldType fieldType) {
        if (fieldType == SchemaFieldDto.FieldType.ARRAY) {
            return false;
        }
        return switch (operator) {
            case "=" -> true;
            case "reg" -> fieldType == SchemaFieldDto.FieldType.TEXT || fieldType == SchemaFieldDto.FieldType.STRING;
            case ">", "<" ->
                    fieldType == SchemaFieldDto.FieldType.DOUBLE || fieldType == SchemaFieldDto.FieldType.LONG || fieldType == SchemaFieldDto.FieldType.DATE;
            default -> throw new IllegalArgumentException(String.format("Operator %s is not supported!", operator));
        };
    }

    private Pair<Object, SchemaFieldDto.FieldType> getValue(String valueExpression, TaskSchemaDto taskSchemaDto, MapJson mapJson) {
        Matcher matcher = VALUE_PATTERN.matcher(valueExpression);
        if (matcher.matches()) {
            String value = matcher.group(1);
            SchemaFieldDto schemaFieldDto = taskSchemaDto.getField(value).orElseThrow(() -> new IllegalArgumentException(String.format("Field with name %s not found in schema. Can't build expression filter!", value)));

            return Pair.of(mapJson.get(value), schemaFieldDto.getFieldType());
        }
        return Pair.of(valueExpression, null);
    }


    @Override
    public boolean test(MapJson map) {
        return predicate.test(map);
    }
}