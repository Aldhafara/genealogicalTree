package com.aldhafara.genealogicalTree.services.gedcom;

import java.util.List;

class GedcomLineIterator {
    private final List<String> lines;
    private int index;

    GedcomLineIterator(List<String> lines) {
        this.lines = lines;
        this.index = 0;
    }

    boolean hasNext() {
        return index < lines.size();
    }

    String peek() {
        return lines.get(index);
    }

    String next() {
        return lines.get(index++);
    }
}

