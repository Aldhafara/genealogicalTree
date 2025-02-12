package com.aldhafara.genealogicalTree.entities;

import com.aldhafara.genealogicalTree.models.SexEnum;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "persons", indexes = {
        @Index(name = "idx_family_id", columnList = "family_id")
})
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Setter
    private UUID addBy;
    @Setter
    private String firstName;
    @Setter
    private String lastName;
    @Setter
    private Instant updateDate;
    @Setter
    private String familyName;
    @Setter
    @Enumerated(EnumType.STRING)
    private SexEnum sex;
    @Setter
    private Instant birthDate;
    @Setter
    private String birthPlace;
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="family_id", insertable = false, updatable = false)
    private Family family;

    public Person() {
    }

    public Person(UUID addBy, String firstName, String lastName) {
        this.addBy = addBy;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Person(Person.Builder builder) {
        this.id = builder.id;
        this.addBy = builder.addBy;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.updateDate = builder.updateDate;
        this.familyName = builder.familyName;
        this.sex = builder.sex;
        this.birthDate = builder.birthDate;
        this.birthPlace = builder.birthPlace;
        this.family = builder.family;
    }

    public static Person.Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID id;
        private UUID addBy;
        private String firstName;
        private String lastName;
        private Instant updateDate;
        private String familyName;
        private SexEnum sex;
        private Instant birthDate;
        private String birthPlace;
        private Family family;

        public Person.Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Person.Builder addBy(UUID addBy) {
            this.addBy = addBy;
            return this;
        }

        public Person.Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Person.Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Person.Builder updateDate(Instant updateDate) {
            this.updateDate = updateDate;
            return this;
        }

        public Person.Builder familyName(String familyName) {
            this.familyName = familyName;
            return this;
        }

        public Person.Builder sex(SexEnum sex) {
            this.sex = sex;
            return this;
        }

        public Person.Builder birthDate(Instant birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public Person.Builder birthPlace(String birthPlace) {
            this.birthPlace = birthPlace;
            return this;
        }

        public Person.Builder family(Family family) {
            this.family = family;
            return this;
        }

        public Person build() {
            return new Person(this);
        }
    }
}
