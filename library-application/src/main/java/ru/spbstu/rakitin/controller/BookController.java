package ru.spbstu.rakitin.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.spbstu.rakitin.client.MdsClient;
import ru.spbstu.rakitin.dto.BookDto;
import ru.spbstu.rakitin.dto.TaskClientDto;

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

}
