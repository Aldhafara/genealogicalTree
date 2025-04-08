package com.aldhafara.genealogicalTree.services.gedcom;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class GedcomToJsonService {
    private final GedcomParser gedcomParser;

    public GedcomToJsonService() {
        this.gedcomParser = new GedcomParser();
    }

    public GedcomData convert(MultipartFile file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
            List<GedcomRecord> result = gedcomParser.parse(lines);
            return gedcomParser.organize(result);
        }
    }
}
