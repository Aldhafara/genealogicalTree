package com.aldhafara.genealogicalTree.services.gedcom;

import com.aldhafara.genealogicalTree.models.gedcom.GedcomData;
import com.aldhafara.genealogicalTree.models.gedcom.GedcomRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class GedcomParserTest {

    private final GedcomParser parser = new GedcomParser();

    @Test
    public void testParseSimpleIndividual() throws JsonProcessingException {
        List<String> lines = readLinesFromResource("gedcom/simpleRequest.ged");

        assert lines != null;
        List<GedcomRecord> records = parser.parse(lines);
        assertFalse(records.isEmpty());

        String expectedJson = readTextFromResource("gedcom/simpleResult.json");
        String actualJson = toJson(records);

        assert expectedJson != null;
        assertEquals(
                normalizeLineEndings(expectedJson),
                normalizeLineEndings(actualJson)
        );
    }

    @Test
    public void testParseCompoundName() {

        List<String> lines = readLinesFromResource("gedcom/compoundNameRequest.ged");

        List<GedcomRecord> records = parser.parse(lines);
        GedcomRecord record = records.get(1);
        Object name = record.toMap().get("name");

        assertTrue(name instanceof Map);

        Map<String, Object> nameMap = (Map<String, Object>) name;
        assertEquals("Anna /Nowak/", nameMap.get("value"));
        assertEquals("Anna", nameMap.get("givn"));
        assertEquals("Nowak", nameMap.get("surn"));
        assertEquals("Kowalska", nameMap.get("_marnm"));
    }

    @Test
    public void testMultipleRecords() {
        List<String> lines = readLinesFromResource("gedcom/multipleRecordsRequest.ged");

        List<GedcomRecord> records = parser.parse(lines);
        assertEquals(3, records.size());
        assertEquals("I1", records.get(0).getId());
        assertEquals("I2", records.get(1).getId());
        assertEquals("F1", records.get(2).getId());
        assertEquals(List.of("I87", "I88", "I90", "I89", "I86"), records.get(2).getDetails().get("chil"));
    }

    @Test
    public void testOrganizeRecords() {
        List<String> lines = readLinesFromResource("gedcom/simpleRequest.ged");

        List<GedcomRecord> records = parser.parse(lines);
        GedcomData data = parser.organize(records);

        assertNotNull(data);
        assertEquals("head", data.gedcomDetails().getType());
        assertEquals(1, data.gedcomPeople().size());
        assertEquals(1, data.gedcomFamilies().size());
        assertEquals(1, data.gedcomSources().size());
    }

    private List<String> readLinesFromResource(String resourceName) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(resourceName)), UTF_8))) {
            return reader.lines().collect(Collectors.toList());
        } catch (IOException e) {
            return null;
        }
    }

    private String readTextFromResource(String resourceName) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(resourceName)), UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            return null;
        }
    }

    private String toJson(List<GedcomRecord> records) throws JsonProcessingException {
        return new com.fasterxml.jackson.databind.ObjectMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(records.stream().map(GedcomRecord::toMap).collect(Collectors.toList()));
    }

    private String normalizeLineEndings(String input) {
        return input.replace("\r\n", "\n").replace("\r", "\n").trim();
    }
}