package com.aldhafara.genealogicalTree.models.gedcom;

import java.util.List;

public record GedcomData(GedcomRecord gedcomDetails, List<GedcomRecord> gedcomPeople,
                         List<GedcomRecord> gedcomFamilies, List<GedcomRecord> gedcomSources) {
}
