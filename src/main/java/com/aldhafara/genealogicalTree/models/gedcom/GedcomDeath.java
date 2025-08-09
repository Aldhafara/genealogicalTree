package com.aldhafara.genealogicalTree.models.gedcom;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class GedcomDeath extends GedcomDetails {
    @JsonProperty
    private String caus;
}
