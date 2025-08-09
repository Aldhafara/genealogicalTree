package com.aldhafara.genealogicalTree.services.gedcom;

import com.aldhafara.genealogicalTree.models.gedcom.GedcomData;
import com.aldhafara.genealogicalTree.models.gedcom.GedcomRecord;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GedcomParser {

    private static final Set<String> MULTI_TAGS = Set.of("CHIL", "OBJE", "RESI", "FAMS", "SOUR");
    private static final Set<String> FORCE_COMPOUND_TAGS = Set.of("BIRT", "DEAT", "EVEN", "CHR", "BAPM", "BURI", "MARR", "NAME");

    public List<GedcomRecord> parse(List<String> lines) {
        GedcomLineIterator it = new GedcomLineIterator(lines);
        List<GedcomRecord> records = new ArrayList<>();

        while (it.hasNext()) {
            String[] mainParts = it.next().split(" ", 4);
            int level = Integer.parseInt(mainParts[0].replaceAll("[^0-9]", "").trim());

            if (level != 0) continue;

            String id = null;
            String type = null;

            if (mainParts.length >= 3 && mainParts[1].startsWith("@") && mainParts[1].endsWith("@")) {
                id = stripAtSymbols(mainParts[1]);
                type = mainParts[2];
            } else if (mainParts.length >= 2) {
                type = mainParts[1];
            }

            GedcomRecord record = new GedcomRecord(id, type);

            while (it.hasNext()) {
                String[] detailParts = it.peek().split(" ", 3);
                int detailLevel = Integer.parseInt(detailParts[0]);
                if (detailLevel <= level) break;

                it.next();
                String tag = detailParts[1];
                String value = detailParts.length == 3 ? stripAtSymbols(detailParts[2]) : "";

                Map<String, Object> compound = new LinkedHashMap<>();
                compound.put("value", value);

                if (it.hasNext() && getLevel(it.peek()) > detailLevel) {
                    compound.putAll(parseCompound(it, detailLevel + 1));
                }

                String tagKey = tag.toLowerCase();

                Object finalValue =
                        compound.size() == 1 && !FORCE_COMPOUND_TAGS.contains(tag.toUpperCase())
                                ? value
                                : compound;

                if (MULTI_TAGS.contains(tag.toUpperCase())) {
                    mergeMultiValue(record, tagKey, finalValue);
                } else {
                    record.addDetail(tag, finalValue);
                }
            }

            records.add(record);
        }

        return records;
    }

    public GedcomData organize(List<GedcomRecord> result) {
        GedcomRecord gedcomDetails = result.get(0);
        List<GedcomRecord> gedcomPersons = new ArrayList<>();
        List<GedcomRecord> gedcomFamilies = new ArrayList<>();
        List<GedcomRecord> gedcomSources = new ArrayList<>();

        for (GedcomRecord record : result) {
            if ("indi".equalsIgnoreCase(record.getType())) gedcomPersons.add(record);
            else if ("fam".equalsIgnoreCase(record.getType())) gedcomFamilies.add(record);
            else if ("sour".equalsIgnoreCase(record.getType())) gedcomSources.add(record);
        }

        return new GedcomData(gedcomDetails, gedcomPersons, gedcomFamilies, gedcomSources);
    }

    private Map<String, Object> parseCompound(GedcomLineIterator it, int level) {
        Map<String, Object> compound = new LinkedHashMap<>();

        while (it.hasNext()) {
            String[] parts = it.peek().split(" ", 3);
            int currentLevel = Integer.parseInt(parts[0]);
            if (currentLevel < level) break;

            it.next();
            String tag = parts[1];
            String value = parts.length == 3 ? stripAtSymbols(parts[2]) : "";

            if (it.hasNext() && getLevel(it.peek()) > currentLevel) {
                compound.put(tag.toLowerCase(), parseCompound(it, currentLevel + 1));
            } else {
                compound.put(tag.toLowerCase(), maybeWrapValue(tag, value));
            }
        }

        return compound;
    }

    private void mergeMultiValue(GedcomRecord record, String tagKey, Object newValue) {
        Object current = record.getDetails().get(tagKey);
        if (current instanceof List) {
            ((List<Object>) current).add(newValue);
        } else if (current != null) {
            record.getDetails().put(tagKey, new ArrayList<>(List.of(current, newValue)));
        } else {
            record.getDetails().put(tagKey, new ArrayList<>(List.of(newValue)));
        }
    }

    private Object maybeWrapValue(String tag, String value) {
        return FORCE_COMPOUND_TAGS.contains(tag.toUpperCase())
                ? new LinkedHashMap<>(Map.of("value", value))
                : value;
    }

    private int getLevel(String line) {
        return Integer.parseInt(line.split(" ", 2)[0]);
    }

    private String stripAtSymbols(String value) {
        if (value != null && value.startsWith("@") && value.endsWith("@") && value.length() > 1) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }
}
