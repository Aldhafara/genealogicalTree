package com.aldhafara.genealogicalTree.services;

import com.aldhafara.genealogicalTree.exceptions.IllegalFileFormatException;
import com.aldhafara.genealogicalTree.exceptions.PersonNotFoundException;
import com.aldhafara.genealogicalTree.models.gedcom.GedcomData;
import com.aldhafara.genealogicalTree.services.gedcom.GedcomImportService;
import com.aldhafara.genealogicalTree.services.gedcom.GedcomToJsonService;
import com.aldhafara.genealogicalTree.services.gedcom.JsonGenerator;
import com.aldhafara.genealogicalTree.services.interfaces.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    private final GedcomToJsonService gedcomToJsonService;
    private final JsonGenerator jsonGenerator;
    private final GedcomImportService importService;

    public FileServiceImpl(GedcomToJsonService gedcomToJsonService,
                           JsonGenerator jsonGenerator,
                           GedcomImportService importService) {
        this.gedcomToJsonService = gedcomToJsonService;
        this.jsonGenerator = jsonGenerator;
        this.importService = importService;
    }

    @Override
    public void analyze(MultipartFile file) throws IllegalFileFormatException {
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.toLowerCase().endsWith(".ged")) {
            throw new IllegalFileFormatException("Illegal file format!");
        }

        long fileSize = file.getSize();
        GedcomData result;

        try {
            result = gedcomToJsonService.convert(file);
            List<String> gedcomPeople = jsonGenerator.generateJsonList(result.gedcomPeople());
            importService.importFromGedcom(gedcomPeople);

            log.info("GEDCOM file processed successfully. fileName={}, fileSizeBytes={}, individuals={}, families={}",
                    fileName, fileSize, result.gedcomPeople().size(), result.gedcomFamilies().size());
        } catch (IOException | PersonNotFoundException e) {
            log.warn("GEDCOM processing failed. fileName={}, fileSizeBytes={}, reason={}",
                    fileName, fileSize, e.getMessage());
            throw new RuntimeException("Failed to process GEDCOM file", e);
        }
    }
}
