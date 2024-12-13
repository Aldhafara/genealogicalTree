package com.aldhafara.genealogicalTree.models;

import com.aldhafara.genealogicalTree.entities.Person;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class FamilyModel {

    private UUID id;
    private UUID addBy;
    private Person father;
    private Person mother;
    private List<Person> children;
    private Instant updateDate;

    public FamilyModel() {
    }

    public FamilyModel(FamilyModel.Builder builder) {
        this.id = builder.id;
        this.addBy = builder.addBy;
        this.father = builder.father;
        this.mother = builder.mother;
        this.children = builder.children;
        this.updateDate = builder.updateDate;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAddBy() {
        return addBy;
    }

    public void setAddBy(UUID addBy) {
        this.addBy = addBy;
    }

    public Person getFather() {
        return father;
    }

    public void setFather(Person father) {
        this.father = father;
    }

    public Person getMother() {
        return mother;
    }

    public void setMother(Person mother) {
        this.mother = mother;
    }

    public List<Person> getChildren() {
        return children;
    }

    public void setChildren(List<Person> children) {
        this.children = children;
    }

    public Instant getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Instant updateDate) {
        this.updateDate = updateDate;
    }


    public static Builder builder() {
        return new Builder();
    }

    public void addChild(Person child) {
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
        return children.stream().map(Person::getId).anyMatch(uuid -> uuid.equals(id));
    }

    public static final class Builder {
        private UUID id;
        private UUID addBy;
        private Person father;
        private Person mother;
        private List<Person> children;
        private Instant updateDate;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }
        public Builder addBy(UUID addBy) {
            this.addBy = addBy;
            return this;
        }

        public Builder father(Person father) {
            this.father = father;
            return this;
        }

        public Builder mother(Person mother) {
            this.mother = mother;
            return this;
        }

        public Builder children(List<Person> children) {
            this.children = children;
            return this;
        }

        public Builder updateDate(Instant updateDate) {
            this.updateDate = updateDate;
            return this;
        }

        public FamilyModel build() {
            return new FamilyModel(this);
        }
    }
}
