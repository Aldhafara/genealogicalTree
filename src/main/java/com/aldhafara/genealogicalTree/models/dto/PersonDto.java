package com.aldhafara.genealogicalTree.models.dto;

import com.aldhafara.genealogicalTree.models.PersonBasicData;
import com.aldhafara.genealogicalTree.models.SexEnum;
import com.aldhafara.genealogicalTree.models.gedcom.MatchResult;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static java.time.ZoneOffset.UTC;

@Getter
@Setter
public class PersonDto {

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
    private List<FamilyDto> familiesAsParent;
    private MatchResult matchResult;

    public PersonDto() {
    }

    public PersonDto(UUID addBy, String firstName, String lastName) {
        this.addBy = addBy;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public PersonDto(Builder builder) {
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
        this.matchResult = builder.matchResult;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Instant getBirthDateAsInstant() {
        return birthDate != null ? birthDate.atStartOfDay(UTC).toInstant() : null;
    }

    public void setBirthDateFromInstant(Instant birthDateInstant) {
        this.birthDate = birthDateInstant != null ?
                LocalDate.ofInstant(birthDateInstant, UTC) : null;
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
        private List<FamilyDto> familiesAsParent;
        private MatchResult matchResult;

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

        public Builder familiesAsParent(List<FamilyDto> familyIdsAsParent) {
            this.familiesAsParent = familyIdsAsParent;
            return this;
        }

        public Builder matchResult(MatchResult matchResult) {
            this.matchResult = matchResult;
            return this;
        }

        public PersonDto build() {
            return new PersonDto(this);
        }
    }
}
