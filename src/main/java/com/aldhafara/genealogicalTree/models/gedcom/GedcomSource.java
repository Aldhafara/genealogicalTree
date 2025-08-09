package com.aldhafara.genealogicalTree.models.gedcom;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class GedcomSource {
    @JsonProperty
    private String value;
    @JsonProperty
    private String page;
    @JsonProperty("quay")
    private String qualityOfData;
    @JsonProperty
    private GedcomDetails data;
    @JsonProperty("even")
    private GedcomEvent event;
}
