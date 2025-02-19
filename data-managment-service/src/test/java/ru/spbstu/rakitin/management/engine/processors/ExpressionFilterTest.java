package ru.spbstu.rakitin.management.engine.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.spbstu.rakitin.commonstarter.dto.FilterExpressionDto;
import ru.spbstu.rakitin.commonstarter.dto.SchemaFieldDto;
import ru.spbstu.rakitin.commonstarter.dto.TaskSchemaDto;
import ru.spbstu.rakitin.commonstarter.dto.TimestampFieldDto;
import ru.spbstu.rakitin.commonstarter.utils.MapJson;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ExpressionFilterTest {

    private static TaskSchemaDto taskSchemaDto;
    private static ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public static void prepare() {
        List<SchemaFieldDto> schemaFieldDtos = Stream.of(
                createField("testStr", SchemaFieldDto.FieldType.STRING),
                createField("testLong", SchemaFieldDto.FieldType.LONG),
                createField("testLong2", SchemaFieldDto.FieldType.LONG),
                createField("testDouble", SchemaFieldDto.FieldType.DOUBLE),
                createField("testDouble2", SchemaFieldDto.FieldType.DOUBLE),
                createField("testStr2", SchemaFieldDto.FieldType.STRING),
                createField("testDate", SchemaFieldDto.FieldType.DATE),
                createField("testDate2", SchemaFieldDto.FieldType.DATE),
                createField("testArr", SchemaFieldDto.FieldType.ARRAY)
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
                createField("age", SchemaFieldDto.FieldType.LONG),
                createField("name", SchemaFieldDto.FieldType.STRING),
                createField("email", SchemaFieldDto.FieldType.STRING)).toList();
        TaskSchemaDto taskSchemaDto2 = TaskSchemaDto.builder()
                .fields(schemaFieldDtos2).timestampField(TimestampFieldDto.builder()
                        .fieldName("timestamp")
                        .useInsertionDate(true).build()).build();

        FilterExpressionDto filterExpressionDto = objectMapper
                .readValue(IOUtils.toString(Objects.requireNonNull(this.getClass().getResourceAsStream("/expression_filter_test1.json")), Charset.defaultCharset()), FilterExpressionDto.class);
        ExpressionFilter expressionFilter = new ExpressionFilter(filterExpressionDto, taskSchemaDto2);
        MapJson map = new MapJson();
        map.put("age", "25");
        map.put("name", "John");
        map.put("email", "john@example.com");
        boolean test = expressionFilter.test(map);
        assertTrue(test);
    }

    private static SchemaFieldDto createField(String fieldName, SchemaFieldDto.FieldType fieldType, SchemaFieldDto.FieldType subType) {
        return SchemaFieldDto.builder()
                .fieldName(fieldName)
                .fieldType(fieldType)
                .subType(subType).build();
    }

    //Map<String, String> map = new HashMap<>();
    //map.put("age", "25");
    //map.put("name", "John");
    //map.put("email", "john@example.com");
    //
    //Predicate<Map<String, String>> predicate = new MapPredicate(
    //    "age > 18 AND (name = 'John' OR email regexp .+@example\\.com)"
    //);
    //
    //boolean result = predicate.test(map); // вернет true

    private static SchemaFieldDto createField(String fieldName, SchemaFieldDto.FieldType fieldType) {
        return SchemaFieldDto.builder()
                .fieldName(fieldName)
                .fieldType(fieldType).build();
    }


}