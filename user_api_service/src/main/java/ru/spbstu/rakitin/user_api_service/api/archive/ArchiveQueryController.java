package ru.spbstu.rakitin.user_api_service.api.archive;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.rakitin.commonstarter.archive.ArchiveServiceManager;
import ru.spbstu.rakitin.dto.archive.FileDto;
import ru.spbstu.rakitin.dto.archive.FileInformationDto;

import java.util.List;

@RestController
@RequestMapping("/api/v1/archive/query")
@RequiredArgsConstructor
@Tag(name = "3. Отправка запроса в архивные задачи")
public class ArchiveQueryController {

    private final ArchiveServiceManager archiveServiceManager;

    @GetMapping("/{configId}/list")
    @Operation(description = "Получение всех файлов, находящихся в директории")
    public List<FileInformationDto> getAllFiles(@PathVariable long configId,
                                                @RequestParam(name = "directory", defaultValue = "") String directory, Authentication authentication) {
        return archiveServiceManager.getAllFilesForTask(configId, directory, authentication);
    }

    @GetMapping("/{configId}/file/{filename}")
    @Operation(description = "Получение определенного файла")
    public FileDto getFile(@PathVariable long configId, @PathVariable String filename, @RequestParam(name = "directory", defaultValue = "") String directory, Authentication authentication) {
        return archiveServiceManager.getFile(configId, filename, directory, authentication);
    }

}
