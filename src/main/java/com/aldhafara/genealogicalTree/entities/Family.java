package com.aldhafara.genealogicalTree.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@Table(name = "families", indexes = {
        @Index(name = "idx_father_id", columnList = "father_id"),
        @Index(name = "idx_mother_id", columnList = "mother_id")
})
public class Family {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Setter
    private UUID addBy;
    @Setter
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "father_id", unique = false)
    private Person father;
    @Setter
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "mother_id", unique = false)
    private Person mother;
    @Setter
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "family_id")
    private List<Person> children = new ArrayList<>();
    @Setter
    private Instant updateDate;

    public Family() {
    }

    public Family(Person father, Person mother, Person child) {
        this.father = father;
        this.mother = mother;
        if (child != null) {
            this.children = List.of(child);
        }
    }

    public Family(Family.Builder builder) {
        this.id = builder.id;
        this.addBy = builder.addBy;
        this.father = builder.father;
        this.mother = builder.mother;
        this.children = builder.children;
        this.updateDate = builder.updateDate;
    }

    @PrePersist
    protected void onCreate() {
        this.updateDate = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateDate = Instant.now();
    }

    public static Family.Builder builder() {
        return new Builder();
    }

    public void addChild(Person child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(child);
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
