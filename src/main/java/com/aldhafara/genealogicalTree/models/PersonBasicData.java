package com.aldhafara.genealogicalTree.models;

import com.aldhafara.genealogicalTree.entities.Person;

import java.util.UUID;

public class PersonBasicData {

    private UUID id;
    private String firstName;
    private String lastName;

    public PersonBasicData(Person person) {
        if (person == null) {
            this.id = null;
            this.firstName = null;
            this.lastName = null;
        } else {
            this.id = person.getId();
            this.firstName = person.getFirstName();
            this.lastName = person.getLastName();
        }
    }

    public PersonBasicData(UUID id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
}
