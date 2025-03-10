package ru.spbstu.rakitin.commonstarter.service.impl;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.commonstarter.dto.FieldType;
import ru.spbstu.rakitin.commonstarter.dto.SchemaFieldDto;
import ru.spbstu.rakitin.commonstarter.dto.TaskSchemaDto;
import ru.spbstu.rakitin.commonstarter.exception.InvalidSchemaException;
import ru.spbstu.rakitin.commonstarter.service.SchemaValidationService;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@ConditionalOnMissingBean(SchemaValidationService.class)
public class SchemaValidationServiceImpl<T extends TaskSchemaDto> implements SchemaValidationService<T> {

    private static final Pattern VALUE_PATTERN = Pattern.compile("\\$\\{(\\S*)}");
    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("(.*)(>|<|=|reg)(.*)");


    @Override
    public void validateSchema(T schema) throws InvalidSchemaException {
        if (!schema.getTimestampField().isUseInsertionDate()) {
            String fieldName = schema.getTimestampField().getFieldName();
            if (schema.getFields().stream().noneMatch(schemaField -> schemaField.getFieldName().equals(fieldName))) {
                throw new InvalidSchemaException(String.format("Specified timestamp field %s not found in schema!", fieldName));
            }
        }

        if (schema.getFilterExpression() != null) {
            List<String> allExpressions = schema.getFilterExpression().getAllExpressions();
            for (String expression : allExpressions) {
                Matcher matcher = EXPRESSION_PATTERN.matcher(expression);
                if (!matcher.matches()) throw new InvalidSchemaException("Expression is not match pattern");
                String left = matcher.group(1);
                String operator = matcher.group(2).trim();
                String right = matcher.group(3);

                FieldType fieldTypeLeft = getFieldTypeForValue(left, schema);
                FieldType fieldTypeRight = getFieldTypeForValue(right, schema);
                if (fieldTypeRight == null || fieldTypeLeft == null) {
                    throw new InvalidSchemaException(String.format("Unable to specify field type for value %s", right));
                }

                if (fieldTypeRight == FieldType.UNDEFINED && fieldTypeLeft.isValueCompatible(right)) {
                    fieldTypeRight = fieldTypeLeft;
                } else if (fieldTypeLeft == FieldType.UNDEFINED && fieldTypeRight.isValueCompatible(left)) {
                    fieldTypeLeft = fieldTypeRight;
                }

                if (fieldTypeRight == FieldType.UNDEFINED) {
                    throw new InvalidSchemaException(String.format("Unable to specify field type for value %s", right));
                }
                if (fieldTypeLeft == FieldType.UNDEFINED) {
                    throw new InvalidSchemaException(String.format("Unable to specify field type for value %s", left));
                }

                if (!isOperatorCompatibleWithType(operator, fieldTypeLeft)) {
                    throw new InvalidSchemaException(String.format("Field with type %s is not compatible with operator '%s'", fieldTypeLeft, operator));
                }
                if (!isOperatorCompatibleWithType(operator, fieldTypeRight)) {
                    throw new InvalidSchemaException(String.format("Field with type %s is not compatible with operator '%s'", fieldTypeRight, operator));
                }

            }
        }
        int schemaSize = schema.getFields().size();
        Set<String> uniqueFields = schema.getFields().stream().map(SchemaFieldDto::getFieldName).collect(Collectors.toUnmodifiableSet());
        if (schemaSize != uniqueFields.size()) {
            throw new InvalidSchemaException("There are duplicates in schema");
        }

    }

    private boolean isOperatorCompatibleWithType(String operator, FieldType fieldType) {
        if (fieldType == FieldType.ARRAY) {
            return false;
        }
        return switch (operator) {
            case "=" -> true;
            case "reg" -> fieldType == FieldType.TEXT || fieldType == FieldType.STRING;
            case ">", "<" ->
                    fieldType == FieldType.DOUBLE || fieldType == FieldType.LONG || fieldType == FieldType.DATE;
            default -> throw new IllegalArgumentException(String.format("Operator %s is not supported!", operator));
        };
    }


    private FieldType getFieldTypeForValue(String value, TaskSchemaDto schema) {
        Matcher matcher = VALUE_PATTERN.matcher(value);
        if (!matcher.matches()) return FieldType.UNDEFINED;

        String fieldName = matcher.group(1);
        Optional<SchemaFieldDto> any = schema.getFields().stream().filter(schemaField -> schemaField.getFieldName().equals(fieldName)).findAny();
        return any.map(SchemaFieldDto::getFieldType).orElse(null);
    }

}
