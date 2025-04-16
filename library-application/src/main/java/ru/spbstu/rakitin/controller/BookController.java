package ru.spbstu.rakitin.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.spbstu.rakitin.client.MdsClient;
import ru.spbstu.rakitin.client.MdsResponse;
import ru.spbstu.rakitin.dto.BookDto;
import ru.spbstu.rakitin.dto.BookSearchRequestDto;
import ru.spbstu.rakitin.dto.MdsBookDto;
import ru.spbstu.rakitin.dto.TaskClientDto;
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
    private final BookService bookService;

    public BookController(MdsClient mdsClient, @Qualifier("taskClientDtoFulltext") TaskClientDto fulltextTask, BookService bookService) {
        this.mdsClient = mdsClient;
        this.fulltextTask = fulltextTask;
        this.bookService = bookService;
    }

    @PostMapping("/save")
    public long saveBook(@RequestBody BookDto bookDto) {
        Book book = bookService.saveBook(bookDto.toBook());
        mdsClient.sendMessageToTask(fulltextTask, MdsBookDto.fromBook(book));

        return book.getId();
    }

    @PostMapping("/find")
    public List<BookDto> findBooksByDescription(@RequestBody BookSearchRequestDto bookSearchRequestDto) {
        MdsResponse<List<Map<String, Object>>> mapMdsResponse = mdsClient.sendRequest(new FulltextQueryRequest(fulltextTask, BookSearchRequestDto.mapToSolrQueryDto(bookSearchRequestDto)));

        return mapMdsResponse.getResponse().get().stream().map(stringObjectMap -> {
            Long id = Long.valueOf(stringObjectMap.get("libraryId").toString());
            Optional<Book> book = bookService.getBook(id);
            return book.map(BookDto::fromBook).orElse(null);
        }).filter(Objects::nonNull).distinct().toList();
    }

}
