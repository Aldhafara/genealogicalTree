package com.aldhafara.genealogicalTree.models.dto;

import com.aldhafara.genealogicalTree.models.RelationshipStatus;
import com.aldhafara.genealogicalTree.models.SexEnum;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class TreeStructuresDto {
    private List<TreeStructure> families = new ArrayList<>();
    private SexEnum mainPersonSex;

    @Data
    public static class TreeStructure {
        private UUID id;
        private PersonDto father;
        private PersonDto mother;
        private Instant marriageDate;
        private RelationshipStatus status;
        private List<PersonDto> children = new ArrayList<>();
    }
}
