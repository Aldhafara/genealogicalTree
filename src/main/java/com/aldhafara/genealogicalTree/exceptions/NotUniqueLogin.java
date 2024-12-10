package com.aldhafara.genealogicalTree.exceptions;

public class NotUniqueLogin extends Exception{
    public NotUniqueLogin() {
    }

    public NotUniqueLogin(String message) {
        super(message);
    }
}
