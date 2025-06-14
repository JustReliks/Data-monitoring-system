package ru.spbstu.rakitin.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.spbstu.rakitin.client.MdsClient;
import ru.spbstu.rakitin.client.MdsResponse;
import ru.spbstu.rakitin.dto.BookSearchRequestDto;
import ru.spbstu.rakitin.dto.SearchTimeDto;
import ru.spbstu.rakitin.dto.TaskClientDto;
import ru.spbstu.rakitin.dto.fulltext.SolrQueryResponseDto;
import ru.spbstu.rakitin.model.Book;
import ru.spbstu.rakitin.repository.BookRepository;
import ru.spbstu.rakitin.requests.fulltext.FulltextQueryRequest;
import ru.spbstu.rakitin.service.BookSearchService;
import ru.spbstu.rakitin.service.BookService;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Primary
public class BookSearchServiceImpl implements BookSearchService {

    private final MdsClient mdsClient;
    private final TaskClientDto fulltextTask;
    private final BookService bookService;
    private final TaskClientDto searchTimeTask;
    private final BookRepository bookRepository;

    public BookSearchServiceImpl(MdsClient mdsClient,
                                 @Qualifier("taskClientDtoFulltext") TaskClientDto fulltextTask, BookService bookService,
                                 @Qualifier("taskClientDtoSearchTimeMonitoring") TaskClientDto searchTimeTask, BookRepository bookRepository) {
        this.mdsClient = mdsClient;
        this.fulltextTask = fulltextTask;
        this.bookService = bookService;
        this.searchTimeTask = searchTimeTask;
        this.bookRepository = bookRepository;
    }

    @Override
    public List<Book> findByDescriptionMds(BookSearchRequestDto searchRequestDto) {
        log.info("Searching for books with description in mds {}", searchRequestDto);
        MdsResponse<SolrQueryResponseDto> mapMdsResponse = mdsClient.sendRequest(new FulltextQueryRequest(fulltextTask, BookSearchRequestDto.mapToSolrQueryDto(searchRequestDto))); // Отправка запроса получению данных из полнотекстового хранилища
        return mapMdsResponse.getResponse().get().getResponse().stream().map(stringObjectMap -> {
                    Long id = Long.valueOf(stringObjectMap.get("libraryId").toString());
                    Optional<Book> book = bookService.getBook(id);
                    return book.orElse(null);
                })
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public List<Book> findByDescriptionDatabase(BookSearchRequestDto searchRequestDto) {
        log.info("Searching for books with description in database {}", searchRequestDto);
        long start = System.currentTimeMillis();
        List<Book> books = bookService.findByDescription(searchRequestDto.getDescription(), searchRequestDto.getLimit());
        long end = System.currentTimeMillis();
        long count = bookRepository.count();
//        mdsClient.sendMessageToTask(searchTimeTask, new SearchTimeDto("db", count, end - start, DateTimeFormatter.ISO_INSTANT.format(new Date().toInstant())));

        return books;
    }
}
