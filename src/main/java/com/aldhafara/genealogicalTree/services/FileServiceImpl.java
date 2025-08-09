package com.aldhafara.genealogicalTree.services;

import com.aldhafara.genealogicalTree.exceptions.IllegalFileFormatException;
import com.aldhafara.genealogicalTree.models.gedcom.GedcomData;
import com.aldhafara.genealogicalTree.services.gedcom.GedcomToJsonService;
import com.aldhafara.genealogicalTree.services.gedcom.JsonGenerator;
import com.aldhafara.genealogicalTree.services.interfaces.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@Service
public class FileServiceImpl implements FileService {

    private static final Logger logger = Logger.getLogger(FileServiceImpl.class.getName());

    private final GedcomToJsonService gedcomToJsonService;
    private final JsonGenerator jsonGenerator;

    public FileServiceImpl(GedcomToJsonService gedcomToJsonService, JsonGenerator jsonGenerator) {
        this.gedcomToJsonService = gedcomToJsonService;
        this.jsonGenerator = jsonGenerator;
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
            List<String> gedcomPersons = jsonGenerator.generateJsonList(result.gedcomPeople());
            System.out.println(gedcomPersons); //TODO Add persons to DB
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        logger.info("Received file: " + fileName);
        logger.info("File size: " + fileSize + " bytes");
        logger.info("Found " + result.gedcomPeople().size() + " individuals");
        logger.info("Found " + result.gedcomFamilies().size() + " families");
    }
}
