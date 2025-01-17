package com.aldhafara.genealogicalTree.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
public class FamilyDto {

    private UUID id;
    private UUID addBy;
    private PersonDto father;
    private PersonDto mother;
    private List<PersonDto> children;
    private Instant updateDate;

    public FamilyDto() {
    }

    public FamilyDto(FamilyDto.Builder builder) {
        this.id = builder.id;
        this.addBy = builder.addBy;
        this.father = builder.father;
        this.mother = builder.mother;
        this.children = builder.children;
        this.updateDate = builder.updateDate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void addChild(PersonDto child) {
        if (children == null || children.isEmpty()) {
            this.children = List.of(child);
        } else {
            this.children.add(child);
        }
    }

    public boolean hasChild(UUID id) {
        if (children == null || children.isEmpty()) {
            return false;
        }
        return children.stream().map(PersonDto::getId).anyMatch(uuid -> uuid.equals(id));
    }

    public static final class Builder {
        private UUID id;
        private UUID addBy;
        private PersonDto father;
        private PersonDto mother;
        private List<PersonDto> children;
        private Instant updateDate;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }
        public Builder addBy(UUID addBy) {
            this.addBy = addBy;
            return this;
        }

        public Builder father(PersonDto father) {
            this.father = father;
            return this;
        }

        public Builder mother(PersonDto mother) {
            this.mother = mother;
            return this;
        }

        public Builder children(List<PersonDto> children) {
            this.children = children;
            return this;
        }

        public Builder updateDate(Instant updateDate) {
            this.updateDate = updateDate;
            return this;
        }

        public FamilyDto build() {
            return new FamilyDto(this);
        }
    }
}
