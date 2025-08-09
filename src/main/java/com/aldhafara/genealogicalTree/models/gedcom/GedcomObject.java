package com.aldhafara.genealogicalTree.models.gedcom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class GedcomObject {
    @JsonProperty
    private String value;
    @JsonProperty("form")
    private String format;
}
