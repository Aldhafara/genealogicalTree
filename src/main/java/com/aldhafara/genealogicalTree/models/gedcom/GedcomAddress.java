package com.aldhafara.genealogicalTree.models.gedcom;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class GedcomAddress {
    @JsonProperty
    private String city;
    @JsonProperty("ctry")
    private String country;
    @JsonProperty("stae")
    private String state;
    @JsonProperty("post")
    private String postalCode;
}
