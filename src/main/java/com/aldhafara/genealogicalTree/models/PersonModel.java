package com.aldhafara.genealogicalTree.models;

import java.util.UUID;

public class PersonModel {

    private UUID id;
    private UserModel addBy;
    private String firstName;
    private String lastName;

    public PersonModel() {
    }

    public PersonModel(UserModel addBy, String firstName, String lastName) {
        this.addBy = addBy;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public PersonModel(Builder builder) {
        this.id = builder.id;
        this.addBy = builder.addBy;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
    }

    public static PersonModel.Builder builder() {
        return new PersonModel.Builder();
    }

    public UUID getId() {
        return id;
    }

    public UserModel getAddBy() {
        return addBy;
    }

    public void setAddBy(UserModel addBy) {
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

    public static final class Builder {
        private UUID id;
        private UserModel addBy;
        private String firstName;
        private String lastName;

        public PersonModel.Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public PersonModel.Builder addBy(UserModel addBy) {
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

        public PersonModel build() {
            return new PersonModel(this);
        }
    }
}
