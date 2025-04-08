package com.aldhafara.genealogicalTree.services.gedcom;

import java.util.List;

public record GedcomData(GedcomRecord gedcomDetails, List<GedcomRecord> gedcomPersons,
                         List<GedcomRecord> gedcomFamilies, List<GedcomRecord> gedcomSources) {
}
