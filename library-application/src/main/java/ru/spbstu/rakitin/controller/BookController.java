package ru.spbstu.rakitin.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.rakitin.client.MdsClient;
import ru.spbstu.rakitin.client.MdsResponse;
import ru.spbstu.rakitin.dto.*;
import ru.spbstu.rakitin.model.Book;
import ru.spbstu.rakitin.requests.fulltext.FulltextQueryRequest;
import ru.spbstu.rakitin.service.BookService;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/book")
public class BookController {

    private final MdsClient mdsClient;


    private final TaskClientDto fulltextTask;
    private final TaskClientDto monitoringTask;
    private final BookService bookService;

    public BookController(MdsClient mdsClient,
                          @Qualifier("taskClientDtoFulltext") TaskClientDto fulltextTask,
                          @Qualifier("taskClientDtoMonitoring") TaskClientDto monitoringTask, BookService bookService) {
        this.mdsClient = mdsClient;
        this.fulltextTask = fulltextTask;
        this.monitoringTask = monitoringTask;
        this.bookService = bookService;
    }

    @PostMapping("/save")
    public long saveBook(@RequestBody BookDto bookDto) {
        Book book = bookService.saveBook(bookDto.toBook());
        mdsClient.sendMessageToTask(fulltextTask, MdsBookDto.fromBook(book));

        return book.getId();
    }

    @PostMapping("/find")
    public List<BookDto> findBooksByDescription(@RequestBody BookSearchRequestDto bookSearchRequestDto,
                                                @RequestParam(required = false, defaultValue = "false") boolean db) {
        MdsResponse<List<Map<String, Object>>> mapMdsResponse = mdsClient.sendRequest(new FulltextQueryRequest(fulltextTask, BookSearchRequestDto.mapToSolrQueryDto(bookSearchRequestDto)));

        if (db) {
            List<Book> books = bookService.findByDescription(bookSearchRequestDto.getDescription());
            return books.stream().map(BookDto::fromBook).toList();
        } else {
            return mapMdsResponse.getResponse().get().stream().map(stringObjectMap -> {
                Long id = Long.valueOf(stringObjectMap.get("libraryId").toString());
                Optional<Book> book = bookService.getBook(id);
                return book.map(BookDto::fromBook).orElse(null);
            }).filter(Objects::nonNull).distinct().toList();
        }
    }

    @PostMapping("/buy/{id}")
    public void buyBook(@PathVariable long id, @RequestParam(required = false, defaultValue = "1", name = "quantity") int quantity) {
        Book book = bookService.getBook(id).orElseThrow();
        BookSellMetricDto bookSellMetricDto = new BookSellMetricDto(book.getTitle(), quantity);
        mdsClient.sendMessageToTask(monitoringTask, bookSellMetricDto);
    }

}
