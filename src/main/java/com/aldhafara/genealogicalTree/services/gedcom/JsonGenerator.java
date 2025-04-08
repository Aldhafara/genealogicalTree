package com.aldhafara.genealogicalTree.services.gedcom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JsonGenerator {
    public List<String> generateJsonList(List<GedcomRecord> records) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> jsonList = new ArrayList<>();

        for (GedcomRecord record : records) {
            ObjectNode jsonNode = objectMapper.valueToTree(record.toMap());
            jsonList.add(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode));
        }

        return jsonList;
    }
}
