package ru.spbstu.rakitin.management.engine.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.spbstu.rakitin.dto.*;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExpressionFilterTest {

    private static TaskSchemaDto taskSchemaDto;
    private static ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public static void prepare() {
        List<SchemaFieldDto> schemaFieldDtos = Stream.of(
                createField("testStr", FieldType.STRING),
                createField("testLong", FieldType.LONG),
                createField("testLong2", FieldType.LONG),
                createField("testDouble", FieldType.DOUBLE),
                createField("testDouble2", FieldType.DOUBLE),
                createField("testStr2", FieldType.STRING),
                createField("testDate", FieldType.DATE),
                createField("testDate2", FieldType.DATE),
                createField("testArr", FieldType.ARRAY)
        ).toList();

        taskSchemaDto = TaskSchemaDto.builder()
                .fields(schemaFieldDtos).timestampField(TimestampFieldDto.builder()
                        .fieldName("timestamp")
                        .useInsertionDate(true).build()).build();
    }

    @SneakyThrows
    @Test
    public void test1() {
        List<SchemaFieldDto> schemaFieldDtos2 = Stream.of(
                createField("age", FieldType.LONG),
                createField("name", FieldType.STRING),
                createField("email", FieldType.STRING)).toList();

        FilterExpression filterExpression = objectMapper
                .readValue(IOUtils.toString(Objects.requireNonNull(this.getClass().getResourceAsStream("/expression_filter_test1.json")), Charset.defaultCharset()), FilterExpression.class);
        TaskSchemaDto taskSchemaDto2 = TaskSchemaDto.builder()
                .fields(schemaFieldDtos2).timestampField(TimestampFieldDto.builder()
                        .fieldName("timestamp")
                        .useInsertionDate(true).build())
                .filterExpression(filterExpression).build();

        ExpressionFilter expressionFilter = new ExpressionFilter(taskSchemaDto2);
        MapJson map = new MapJson();
        map.put("age", "25");
        map.put("name", "John");
        map.put("email", "john@example.com");
        boolean test = expressionFilter.test(null, map);
        assertTrue(test);
    }

    @SneakyThrows
    @Test
    public void test2() {
        List<SchemaFieldDto> schemaFieldDtos2 = Stream.of(
                createField("age", FieldType.LONG),
                createField("name", FieldType.STRING),
                createField("email", FieldType.STRING)).toList();

        FilterExpression filterExpression = objectMapper
                .readValue(IOUtils.toString(Objects.requireNonNull(this.getClass().getResourceAsStream("/expression_filter_test2.json")), Charset.defaultCharset()), FilterExpression.class);
        TaskSchemaDto taskSchemaDto2 = TaskSchemaDto.builder()
                .fields(schemaFieldDtos2).timestampField(TimestampFieldDto.builder()
                        .fieldName("timestamp")
                        .useInsertionDate(true).build())
                .filterExpression(filterExpression).build();
        ExpressionFilter expressionFilter = new ExpressionFilter(taskSchemaDto2);
        MapJson map = new MapJson();
        map.put("age", "1");
        map.put("name", "John");
        map.put("email", "john@example.com");
        boolean test = expressionFilter.test(map);
        assertTrue(test);
    }

    @SneakyThrows
    @Test
    public void test3() {
        ////testLong>18 AND testStr=John OR testStr=testStr2
        FilterExpression filterExpression = objectMapper
                .readValue(IOUtils.toString(Objects.requireNonNull(this.getClass().getResourceAsStream("/expression_filter_test3.json")), Charset.defaultCharset()), FilterExpression.class);
        taskSchemaDto.setFilterExpression(filterExpression);
        ExpressionFilter expressionFilter = new ExpressionFilter(taskSchemaDto);
        MapJson map = new MapJson();
        map.put("testLong", 15);
        map.put("testStr", "NotJohn");
        map.put("testStr2", "NotJohn2");
        boolean test = expressionFilter.test(map);
        assertFalse(test);
        taskSchemaDto.setFilterExpression(null);
    }


    private static SchemaFieldDto createField(String fieldName, FieldType fieldType, FieldType subType) {
        return SchemaFieldDto.builder()
                .fieldName(fieldName)
                .fieldType(fieldType)
                .subType(subType).build();
    }

    private static SchemaFieldDto createField(String fieldName, FieldType fieldType) {
        return createField(fieldName, fieldType, null);
    }


}