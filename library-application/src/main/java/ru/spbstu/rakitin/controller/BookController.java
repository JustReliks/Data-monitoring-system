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
import ru.spbstu.rakitin.dto.TaskClientDto;
import ru.spbstu.rakitin.requests.fulltext.FulltextQueryRequest;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/book")
public class BookController {

    private final MdsClient mdsClient;


    private final TaskClientDto fulltextTask;

    public BookController(MdsClient mdsClient, @Qualifier("taskClientDtoFulltext") TaskClientDto fulltextTask) {
        this.mdsClient = mdsClient;
        this.fulltextTask = fulltextTask;
    }

    @PostMapping("/save")
    public void saveBook(@RequestBody BookDto bookDto) {
        mdsClient.sendMessageToTask(fulltextTask, bookDto);
    }

    @PostMapping("/find")
    public List<BookDto> findBooksByDescription(@RequestBody BookSearchRequestDto bookSearchRequestDto) {
        MdsResponse<List<Map<String, Object>>> mapMdsResponse = mdsClient.sendRequest(new FulltextQueryRequest(fulltextTask, BookSearchRequestDto.mapToSolrQueryDto(bookSearchRequestDto)));
        List<BookDto> list = mapMdsResponse.getResponse().get().stream().map(stringObjectMap -> {
            BookDto bookDto = new BookDto();
            List<String> description = (List<String>) stringObjectMap.get("description");
            bookDto.setDescription(String.join("\n", description));
            bookDto.setTitle((String) stringObjectMap.get("title"));
            bookDto.setAuthor((String) stringObjectMap.get("author"));
            bookDto.setPrice((Double) stringObjectMap.get("price"));
            bookDto.setPublisher((String) stringObjectMap.get("publisher"));
            bookDto.setPublishedDate((String) stringObjectMap.get("publishedDate"));

            return bookDto;
        }).distinct().toList();

        return list;
    }

}
