package ru.spbstu.rakitin.controller;

import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.rakitin.client.MdsClient;
import ru.spbstu.rakitin.client.MdsResponse;
import ru.spbstu.rakitin.dto.*;
import ru.spbstu.rakitin.dto.archive.FileDto;
import ru.spbstu.rakitin.model.Book;
import ru.spbstu.rakitin.repository.BookRepository;
import ru.spbstu.rakitin.requests.archive.GetFileRequest;
import ru.spbstu.rakitin.service.BookSearchService;
import ru.spbstu.rakitin.service.BookService;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/book")
public class BookController {

    private final MdsClient mdsClient;
    private final TaskClientDto monitoringTask;

    private final TaskClientDto fulltextTask;
    private final TaskClientDto pageTask;
    private final BookService bookService;
    private final BookSearchService bookSearchService;
    private final BookRepository bookRepository;

    public BookController(MdsClient mdsClient,
                          @Qualifier("taskClientDtoFulltext") TaskClientDto fulltextTask,
                          @Qualifier("taskClientDtoMonitoring") TaskClientDto monitoringTask,
                          @Qualifier("pageTaskClientDto") TaskClientDto pageTask, BookService bookService,
                          BookSearchService bookSearchService, BookRepository bookRepository) {
        this.mdsClient = mdsClient;
        this.fulltextTask = fulltextTask;
        this.monitoringTask = monitoringTask;
        this.pageTask = pageTask;
        this.bookService = bookService;
        this.bookSearchService = bookSearchService;
        this.bookRepository = bookRepository;
    }

    @PostMapping("/save")
    public long saveBook(@RequestBody BookDto bookDto) {
        Book book = bookService.saveBook(bookDto.toBook());
        mdsClient.sendMessageToTask(fulltextTask, MdsBookDto.fromBook(book));
        return book.getId();
    }

    @GetMapping("/{bookId}/page/{page}")
    public PageDto getPage(@PathVariable long bookId, @PathVariable int page) {
        Book book = bookRepository.findById(bookId).orElseThrow();
        String title = book.getTitle().replace(" ", "_");
        GetFileRequest getFileRequest = new GetFileRequest(pageTask, title, title + "_" + page);
        MdsResponse<FileDto> fileDtoMdsResponse = mdsClient.sendRequest(getFileRequest);
        FileDto fileDto = fileDtoMdsResponse.getResponse().get();
        PageDto pageDto = new PageDto();
        pageDto.setContent(fileDto.getFileData().get("content").toString());
        pageDto.setPage(Integer.parseInt(fileDto.getFileData().get("page").toString()));

        return pageDto;
    }

    @PostMapping("/{bookId}/page/save")
    public void savePage(
            @PathVariable long bookId, @RequestBody PageDto page) {

        Book book = bookRepository.findById(bookId).orElseThrow();
        String content = page.getContent();
        int pageNum = page.getPage();

        PageMessageDto pageMessageDto = new PageMessageDto();
        pageMessageDto.setContent(content);
        pageMessageDto.setPage(pageNum);
        pageMessageDto.setBookPath("/" + book.getTitle().replace(" ", "_"));
        pageMessageDto.setFilename(book.getTitle().replace(" ", "_") + "_" + pageNum);

        mdsClient.sendMessageToTask(pageTask, pageMessageDto);
    }

    @PostMapping("/find")
    public List<BookDto> findBooksByDescription(@RequestBody BookSearchRequestDto bookSearchRequestDto,
                                                @RequestParam(required = false, defaultValue = "false") boolean db) {
        List<Book> books;
        if (db) {
            books = bookSearchService.findByDescriptionDatabase(bookSearchRequestDto);
        } else {
            books = bookSearchService.findByDescriptionMds(bookSearchRequestDto);
        }
        return books.stream().map(BookDto::fromBook).collect(Collectors.toList());
    }

    DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;

    // Начало предыдущего дня в UTC

    @PostMapping("/buy/{id}")
    public void buyBook(@PathVariable long id, @RequestParam(required = false, defaultValue = "1", name = "quantity") int quantity) {
        Book book = bookService.getBook(id).orElseThrow();
        String currentTime = formatter.format(ZonedDateTime.now(ZoneOffset.UTC).toLocalDate());
        BookSellMetricDto bookSellMetricDto = new BookSellMetricDto(book.getTitle(), quantity, currentTime);
        mdsClient.sendMessageToTask(monitoringTask, bookSellMetricDto);
    }

    Date startDate = new Date(110, 0, 1);  // 2010-01-01
    Date endDate = new Date(125, 11, 31);  // 2025-12-31
    Faker faker = new Faker(new Locale("en"));
    Random random = new Random();


    @PostMapping("/generate")
    public void generateBooks(@RequestParam("count") int booksCount) {
        List<Book> books = new ArrayList<>();

        for (int i = 0; i < booksCount; i++) {
            String title = faker.book().title();
            String description = faker.lorem().sentence(10, 10);
            String author = faker.book().author();
            String publisher = faker.book().publisher();
            Date publishedDate = faker.date().between(startDate, endDate);
            double price = 10 + (100 - 10) * random.nextDouble();
            Book book = new Book();
            book.setTitle(title);
            book.setDescription(description);
            book.setAuthor(author);
            book.setPublisher(publisher);
            book.setPrice(price);
            book.setPublishedDate(publishedDate);
            books.add(book);
        }
        bookRepository.saveAll(books);
        books.parallelStream().forEach(book -> mdsClient.sendMessageToTask(fulltextTask, MdsBookDto.fromBook(book)));
    }

}
