package com.aldhafara.genealogicalTree.models.gedcom;

import java.time.Instant;

public record ParsedDate(Instant instant, Precision precision, String originalText, String note) {
}
