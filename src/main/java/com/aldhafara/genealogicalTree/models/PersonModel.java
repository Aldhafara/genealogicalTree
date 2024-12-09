package com.aldhafara.genealogicalTree.models;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

public class PersonModel {

    private UUID id;
    private UUID addBy;
    private String firstName;
    private String lastName;
    private Instant updateDate;
    private String familyName = "";
    private SexEnum sex;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    private String birthPlace = "";

    public PersonModel() {
    }

    public PersonModel(UUID addBy, String firstName, String lastName) {
        this.addBy = addBy;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public PersonModel(Builder builder) {
        this.id = builder.id;
        this.addBy = builder.addBy;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.updateDate = builder.updateDate;
        this.familyName = builder.familyName;
        this.sex = builder.sex;
        this.birthDate = builder.birthDate;
        this.birthPlace = builder.birthPlace;
    }

    public static PersonModel.Builder builder() {
        return new PersonModel.Builder();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Instant getBirthDateAsInstant() {
        return birthDate != null ? birthDate.atStartOfDay(ZoneId.systemDefault()).toInstant() : null;
    }

    public void setBirthDateFromInstant(Instant birthDateInstant) {
        this.birthDate = birthDateInstant != null ?
                LocalDate.ofInstant(birthDateInstant, ZoneId.systemDefault()) : null;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
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

    @Override
    public String toString() {
        return "PersonModel{" +
                "id=" + id +
                ", addBy=" + addBy +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", updateDate=" + updateDate +
                ", familyName='" + familyName + '\'' +
                ", sex=" + sex +
                ", birthDate=" + birthDate +
                ", birthPlace='" + birthPlace + '\'' +
                '}';
    }

    public static final class Builder {
        private UUID id;
        private UUID addBy;
        private String firstName;
        private String lastName;
        private Instant updateDate;
        private String familyName;
        private SexEnum sex;
        private LocalDate birthDate;
        private String birthPlace;

        public PersonModel.Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public PersonModel.Builder addBy(UUID addBy) {
            this.addBy = addBy;
            return this;
        }

        public PersonModel.Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public PersonModel.Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public PersonModel.Builder updateDate(Instant updateDate) {
            this.updateDate = updateDate;
            return this;
        }

        public PersonModel.Builder familyName(String familyName) {
            this.familyName = familyName;
            return this;
        }

        public PersonModel.Builder sex(SexEnum sex) {
            this.sex = sex;
            return this;
        }

        public PersonModel.Builder birthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public PersonModel.Builder birthPlace(String birthPlace) {
            this.birthPlace = birthPlace;
            return this;
        }

        public PersonModel.Builder setBirthDateFromInstant(Instant birthDateInstant) {
            this.birthDate = birthDateInstant != null ?
                    LocalDate.ofInstant(birthDateInstant, ZoneId.systemDefault()) : null;
            return this;
        }

        public PersonModel build() {
            return new PersonModel(this);
        }
    }
}
