package com.aldhafara.genealogicalTree.models.gedcom;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class GedcomDetails {
    @JsonProperty
    private String value;
    @JsonProperty
    private String date;
    @JsonProperty("plac")
    private String location;
    @JsonProperty
    private String note;
    @JsonProperty
    private String text;
    @JsonProperty
    private String age;
}
