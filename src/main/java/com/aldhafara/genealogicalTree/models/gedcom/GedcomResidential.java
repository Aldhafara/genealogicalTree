package com.aldhafara.genealogicalTree.models.gedcom;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class GedcomResidential {
    @JsonProperty
    private String value;
    @JsonProperty("addr")
    private GedcomAddress address;
    @JsonProperty
    private String email;
}
