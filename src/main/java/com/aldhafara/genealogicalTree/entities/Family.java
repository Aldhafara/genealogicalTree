package com.aldhafara.genealogicalTree.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="families")
public class Family {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private UUID id;
    private UUID addBy;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="father_id")
    private Person father;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="mother_id")
    private Person mother;
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name="family_id")
    private List<Person> children;
    private Instant updateDate;

    public Family() {
    }

    public Family(Family.Builder builder) {
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


    public static Family.Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID id;
        private UUID addBy;
        private Person father;
        private Person mother;
        private List<Person> children;
        private Instant updateDate;

        public Family.Builder id(UUID id) {
            this.id = id;
            return this;
        }
        public Family.Builder addBy(UUID addBy) {
            this.addBy = addBy;
            return this;
        }

        public Family.Builder father(Person father) {
            this.father = father;
            return this;
        }

        public Family.Builder mother(Person mother) {
            this.mother = mother;
            return this;
        }

        public Family.Builder children(List<Person> children) {
            this.children = children;
            return this;
        }

        public Family.Builder updateDate(Instant updateDate) {
            this.updateDate = updateDate;
            return this;
        }

        public Family build() {
            return new Family(this);
        }
    }
}
