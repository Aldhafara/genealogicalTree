package com.aldhafara.genealogicalTree.models.gedcom;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode
@Getter
public class GedcomRecord {
    private final String id;
    private final String type;
    private final Map<String, Object> details = new LinkedHashMap<>();

    public GedcomRecord(String id, String type) {
        this.id = id;
        this.type = type.toLowerCase();
    }

    public void addDetail(String key, Object value) {
        key = key.toLowerCase();
        if (details.containsKey(key)) {
            Object existing = details.get(key);
            if (existing instanceof List) {
                ((List<Object>) existing).add(value);
            } else {
                List<Object> list = new ArrayList<>();
                list.add(existing);
                list.add(value);
                details.put(key, list);
            }
        } else {
            details.put(key, value);
        }
    }

    public Map<String, Object> toMap() {
        Map<String, Object> json = new LinkedHashMap<>();
        json.put(type, id);
        details.forEach(json::put);
        return json;
    }
}
