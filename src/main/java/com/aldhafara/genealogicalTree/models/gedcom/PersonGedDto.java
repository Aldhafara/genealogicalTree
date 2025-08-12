package com.aldhafara.genealogicalTree.models.gedcom;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class PersonGedDto {
    @JsonProperty("indi")
    private String individual;
    @JsonProperty("_upd")
    private String updated;
    private GedcomName name;
    private Character sex;
    private GedcomDetails birt;
    private GedcomDeath deat;
    @JsonProperty("buri")
    private GedcomDetails burial;
    @JsonProperty("emig")
    private GedcomDetails emigration;
    @JsonProperty("fams")
    private List<String> familyAsParent;
    @JsonProperty("famc")
    private String familyAsChild;
    @JsonProperty("sour")
    private List<GedcomSource> source;
    @JsonProperty
    private String rin;
    @JsonProperty("_uid")
    private String id;
    @JsonProperty("obje")
    private List<GedcomObject> objects;
    @JsonProperty("educ")
    private GedcomDetails education;
    @JsonProperty("chr")
    private GedcomDetails baptism;
    @JsonProperty("even")
    private GedcomEvent event;
    @JsonProperty("resi")
    private List<GedcomResidential> residential;
}
