package com.aldhafara.genealogicalTree.validation;

public final class ValidationPatterns {
    private ValidationPatterns() {}

    public static final String HUMAN_NAME = "^[\\p{L}\\p{M}]+(?:[ '-][\\p{L}\\p{M}]+)*$";
}
