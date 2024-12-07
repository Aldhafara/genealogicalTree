package com.aldhafara.genealogicalTree.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "persons")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private RegisterUser addBy;
    private String firstName;
    private String lastName;

    public Person() {
    }

    public Person(RegisterUser addBy, String firstName, String lastName) {
        this.addBy = addBy;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Person(Person.Builder builder) {
        this.id = builder.id;
        this.addBy = builder.addBy;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
    }

    public static Person.Builder builder() {
        return new Person.Builder();
    }

    public UUID getId() {
        return id;
    }

    public RegisterUser getAddBy() {
        return addBy;
    }

    public void setAddBy(RegisterUser addBy) {
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
        private RegisterUser addBy;
        private String firstName;
        private String lastName;

        public Person.Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Person.Builder addBy(RegisterUser addBy) {
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

        public Person build() {
            return new Person(this);
        }
    }
}
