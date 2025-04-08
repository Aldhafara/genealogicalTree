package com.aldhafara.genealogicalTree.services.gedcom;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

public class GedcomParser {

    public List<GedcomRecord> parse(List<String> lines) {
        List<GedcomRecord> records = new ArrayList<>();

        int index = 0;
        while (index < lines.size()) {
            String currentLine = lines.get(index);
            String[] parts = currentLine.split(" ", 4);
            int level = Integer.parseInt(parts[0].replaceAll("[^0-9]", "").trim());

            if (level == 0) {
                String id = null;
                String type = null;
                if (parts.length >= 3 && parts[1].startsWith("@") && parts[1].endsWith("@")) {
                    id = stripAtSymbols(parts[1]);
                    type = parts[2];
                } else if (parts.length >= 2) {
                    type = parts[1];
                }
                GedcomRecord record = new GedcomRecord(id, type);
                index++;
                while (index < lines.size()) {
                    String detailLine = lines.get(index);
                    String[] detailParts = detailLine.split(" ", 3);
                    int detailLevel = Integer.parseInt(detailParts[0]);
                    if (detailLevel <= level) {
                        break;
                    }
                    String tag = detailParts[1];
                    String value = detailParts.length == 3 ? stripAtSymbols(detailParts[2]) : "";

                    Map<String, String> compound = new LinkedHashMap<>();
                    compound.put("value", value);
                    int subLevel = detailLevel + 1;
                    index++;
                    while (index < lines.size()) {
                        String subLine = lines.get(index);
                        String[] subParts = subLine.split(" ", 3);
                        int subLineLevel = Integer.parseInt(subParts[0]);
                        if (subLineLevel < subLevel) {
                            break;
                        }
                        if (subLineLevel == subLevel) {
                            String subTag = subParts[1];
                            String subValue = subParts.length == 3 ? stripAtSymbols(subParts[2]) : "";
                            compound.put(subTag.toLowerCase(), subValue);
                        }
                        index++;
                    }
                    if (compound.size() == 1) {
                        record.addDetail(tag, value);
                    } else {
                        record.addDetail(tag, compound);
                    }
                }
                records.add(record);
            } else {
                index++;
            }
        }
        return records;
    }

    private String stripAtSymbols(String value) {
        if (value != null && value.startsWith("@") && value.endsWith("@") && value.length() > 1) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    public GedcomData organize(List<GedcomRecord> result) {
        GedcomRecord gedcomDetails = result.get(0);
        List<GedcomRecord> gedcomPersons = new ArrayList<>(emptyList());
        List<GedcomRecord> gedcomFamilies = new ArrayList<>(emptyList());
        List<GedcomRecord> gedcomSources = new ArrayList<>(emptyList());
        for (GedcomRecord record : result) {
            if (record.getType().equalsIgnoreCase("indi")) {
                gedcomPersons.add(record);
            }
            if (record.getType().equals("fam")) {
                gedcomFamilies.add(record);
            }
            if (record.getType().equals("sour")) {
                gedcomSources.add(record);
            }
        }
        return new GedcomData(gedcomDetails, gedcomPersons, gedcomFamilies, gedcomSources);
    }
}
