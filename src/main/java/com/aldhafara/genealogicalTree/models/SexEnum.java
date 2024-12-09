package com.aldhafara.genealogicalTree.models;

public enum SexEnum {

    MALE('M', "mężczyzna"),
    FEMALE('F', "kobieta");

    private char sex;
    private String alternativeName;

    SexEnum() {
    }

    SexEnum(char sex) {
        this.sex = sex;
    }

    SexEnum(char sex, String alternativeName) {
        this.sex = sex;
        this.alternativeName = alternativeName;
    }

    public int getSex() {
        return sex;
    }

    public String getAlternativeName() {
        return alternativeName;
    }
}
