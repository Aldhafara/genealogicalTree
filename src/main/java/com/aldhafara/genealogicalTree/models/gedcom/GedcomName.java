package com.aldhafara.genealogicalTree.models.gedcom;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class GedcomName {
    @JsonProperty
    private String value;
    @JsonProperty("givn")
    private String given;
    @JsonProperty("surn")
    private String lastName;
    @JsonProperty("_marnm")
    private String marrigeName;
    @JsonProperty("_rname")
    private String confirmationName;
    @JsonProperty("nsfx")
    private String suffixName;
}
