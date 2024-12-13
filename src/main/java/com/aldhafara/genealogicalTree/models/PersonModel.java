package com.aldhafara.genealogicalTree.models;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static java.time.ZoneOffset.UTC;

public class PersonModel {

    private UUID id;
    private UUID addBy;
    private String firstName;
    private String lastName;
    private Instant updateDate;
    private String familyName;
    private SexEnum sex;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    private String birthPlace;
    private List<PersonBasicData> children;
    private List<PersonBasicData> siblings;
    private List<PersonBasicData> siblingsWithStepSiblings;
    private PersonBasicData mother;
    private PersonBasicData father;
    private List<PersonBasicData> partners;
    private UUID familyId;
    private List<FamilyModel> familiesAsParent;

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
        this.children = builder.children;
        this.siblings = builder.siblings;
        this.mother = builder.mother;
        this.father = builder.father;
        this.partners = builder.partners;
        this.familiesAsParent = builder.familiesAsParent;
    }

    public static Builder builder() {
        return new Builder();
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
        return birthDate != null ? birthDate.atStartOfDay(UTC).toInstant() : null;
    }

    public void setBirthDateFromInstant(Instant birthDateInstant) {
        this.birthDate = birthDateInstant != null ?
                LocalDate.ofInstant(birthDateInstant, UTC) : null;
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

    public List<PersonBasicData> getChildren() {
        return children;
    }

    public void setChildren(List<PersonBasicData> children) {
        this.children = children;
    }

    public List<PersonBasicData> getSiblings() {
        return siblings;
    }

    public void setSiblings(List<PersonBasicData> siblings) {
        this.siblings = siblings;
    }

    public List<PersonBasicData> getSiblingsWithStepSiblings() {
        return siblingsWithStepSiblings;
    }

    public void setSiblingsWithStepSiblings(List<PersonBasicData> siblingsWithStepSiblings) {
        this.siblingsWithStepSiblings = siblingsWithStepSiblings;
    }

    public PersonBasicData getMother() {
        return mother;
    }

    public void setMother(PersonBasicData mother) {
        this.mother = mother;
    }

    public PersonBasicData getFather() {
        return father;
    }

    public void setFather(PersonBasicData father) {
        this.father = father;
    }

    public List<PersonBasicData> getPartners() {
        return partners;
    }

    public void setPartners(List<PersonBasicData> partners) {
        this.partners = partners;
    }

    public UUID getFamilyId() {
        return familyId;
    }

    public void setFamilyId(UUID familyId) {
        this.familyId = familyId;
    }

    public List<FamilyModel> getFamiliesAsParent() {
        return familiesAsParent;
    }

    public void setFamiliesAsParent(List<FamilyModel> familiesAsParent) {
        this.familiesAsParent = familiesAsParent;
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
        private List<PersonBasicData> children;
        private List<PersonBasicData> siblings;
        private PersonBasicData mother;
        private PersonBasicData father;
        private List<PersonBasicData> partners;
        private List<FamilyModel> familiesAsParent;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder addBy(UUID addBy) {
            this.addBy = addBy;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder updateDate(Instant updateDate) {
            this.updateDate = updateDate;
            return this;
        }

        public Builder familyName(String familyName) {
            this.familyName = familyName;
            return this;
        }

        public Builder sex(SexEnum sex) {
            this.sex = sex;
            return this;
        }

        public Builder birthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public Builder birthPlace(String birthPlace) {
            this.birthPlace = birthPlace;
            return this;
        }

        public Builder setBirthDateFromInstant(Instant birthDateInstant) {
            this.birthDate = birthDateInstant != null ?
                    LocalDate.ofInstant(birthDateInstant, UTC) : null;
            return this;
        }

        public Builder children(List<PersonBasicData> children) {
            this.children = children;
            return this;
        }

        public Builder siblings(List<PersonBasicData> siblings) {
            this.siblings = siblings;
            return this;
        }

        public Builder mother(PersonBasicData mother) {
            this.mother = mother;
            return this;
        }

        public Builder father(PersonBasicData father) {
            this.father = father;
            return this;
        }

        public Builder partners(List<PersonBasicData> partners) {
            this.partners = partners;
            return this;
        }

        public Builder familiesAsParent(List<FamilyModel> familyIdsAsParent) {
            this.familiesAsParent = familyIdsAsParent;
            return this;
        }

        public PersonModel build() {
            return new PersonModel(this);
        }
    }
}
