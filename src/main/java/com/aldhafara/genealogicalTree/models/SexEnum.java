package com.aldhafara.genealogicalTree.models;

import lombok.Getter;

public enum SexEnum {

    MALE('M', "mężczyzna"),
    FEMALE('F', "kobieta");

    private final char sex;
    @Getter
    private final String alternativeName;

    SexEnum(char sex, String alternativeName) {
        this.sex = sex;
        this.alternativeName = alternativeName;
    }

    public int getSex() {
        return sex;
    }

    public static SexEnum fromChar(Character c) {
        if (c == null) return null;
        for (SexEnum s : values()) {
            if (s.sex == c) return s;
        }
        throw new IllegalArgumentException("Invalid SexEnum: " + c);
    }
}
