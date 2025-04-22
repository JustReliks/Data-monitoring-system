package ru.spbstu.rakitin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.spbstu.rakitin.client.MdsClient;
import ru.spbstu.rakitin.controller.BookController;
import ru.spbstu.rakitin.dto.BookSearchRequestDto;
import ru.spbstu.rakitin.dto.BookSellMetricDto;
import ru.spbstu.rakitin.dto.TaskClientDto;
import ru.spbstu.rakitin.model.Book;
import ru.spbstu.rakitin.repository.BookRepository;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//@Component
public class FulltextSearchBenchmark implements CommandLineRunner {

    private final BookController bookController;
    private final BookRepository bookRepository;
    private final MdsClient mdsClient;
    private final TaskClientDto monitoringTask;


    private static final String KEYWORD = "magic";
    private static final int LIMIT = 100;
    private static final int SEARCH_COUNT = 15;
    private static final int SEARCH_INTERVAL_MS = 2100;
    private static final String SEARCH_PATTERN = "%s%s%s";

    public FulltextSearchBenchmark(BookController bookController, BookRepository bookRepository, MdsClient mdsClient,
                                   @Qualifier("taskClientDtoMonitoring") TaskClientDto monitoringTask) {
        this.bookController = bookController;
        this.bookRepository = bookRepository;
        this.mdsClient = mdsClient;
        this.monitoringTask = monitoringTask;
    }

    @Override
    public void run(String... args) throws Exception {
        Random random = new Random();
        List<Book> all = bookRepository.findAll();
        List<String> dates = generateIsoInstantTimestamps();
        dates.forEach(date -> {
            BookSellMetricDto bookSellMetricDto = new BookSellMetricDto(all.get(random.nextInt(all.size())).getTitle(), random.nextInt(15)+1, date);
            mdsClient.sendMessageToTask(monitoringTask, bookSellMetricDto);
        });

    }

    public static List<String> generateIsoInstantTimestamps() {
        List<String> timestamps = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;

        // Начало предыдущего дня в UTC
        ZonedDateTime start = ZonedDateTime.now(ZoneOffset.UTC).toLocalDate().atStartOfDay(ZoneOffset.UTC);
        // Конец текущего дня в UTC (начало завтрашнего дня)
        ZonedDateTime end = ZonedDateTime.now(ZoneOffset.UTC).plusDays(1).toLocalDate().atStartOfDay(ZoneOffset.UTC);

        ZonedDateTime current = start;
        Duration interval = Duration.ofMinutes(15);

        while (!current.isAfter(end)) {
            timestamps.add(formatter.format(current));
            current = current.plus(interval);
        }

        return timestamps;
    }

}
