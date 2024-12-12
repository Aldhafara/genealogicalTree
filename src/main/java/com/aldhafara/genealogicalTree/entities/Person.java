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

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "persons", indexes = {
        @Index(name = "idx_family_id", columnList = "family_id")
})
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private UUID addBy;
    private String firstName;
    private String lastName;
    private Instant updateDate;
    private String familyName;
    @Enumerated(EnumType.STRING)
    private SexEnum sex;
    private Instant birthDate;
    private String birthPlace;
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

    public UUID getId() {
        return id;
    }

    public UUID getAddBy() {
        return addBy;
    }

    public void setAddBy(UUID addBy) {
        this.addBy = addBy;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Instant getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Instant updateDate) {
        this.updateDate = updateDate;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public SexEnum getSex() {
        return sex;
    }

    public void setSex(SexEnum sex) {
        this.sex = sex;
    }

    public Instant getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Instant birthDate) {
        this.birthDate = birthDate;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public Family getFamily() {
        return family;
    }

    public void setFamily(Family family) {
        this.family = family;
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
