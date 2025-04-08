package com.aldhafara.genealogicalTree.controllers.api;

import com.aldhafara.genealogicalTree.exceptions.IllegalFileFormatException;
import com.aldhafara.genealogicalTree.services.FileServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/api/file")
@Tag(name = "File Controller", description = "Operations related to files")
public class FileApiController {
    private final FileServiceImpl fileService;

    public FileApiController(FileServiceImpl fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty!");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.toLowerCase().endsWith(".ged")) {
            return ResponseEntity.badRequest().body("Illegal file format!");
        }
        try {
            fileService.analyze(file);
        } catch (IllegalFileFormatException e) {
            return ResponseEntity.badRequest().body("Illegal file format!");
        }
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, "/family-tree")
                .build();
    }
}
