package ru.spbstu.rakitin;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.spbstu.rakitin.configuration.ProjectConfiguration;
import ru.spbstu.rakitin.dto.fulltext.FulltextTaskResponse;
import ru.spbstu.rakitin.requests.fulltext.FulltextTaskListRequest;

import java.util.List;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final MdsClient mdsClient;
    private final ProjectConfiguration projectConfiguration;

    @GetMapping("/all")
    public List<FulltextTaskResponse> findAllConfigs() {
        MdsResponse<List<FulltextTaskResponse>> listMdsResponse = mdsClient.sendRequest(new FulltextTaskListRequest(projectConfiguration.getId()));
        return listMdsResponse.getResponse().get();
    }

}
