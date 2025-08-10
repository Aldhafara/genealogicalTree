package com.aldhafara.genealogicalTree.services.person;

import com.aldhafara.genealogicalTree.entities.Person;
import com.aldhafara.genealogicalTree.models.SexEnum;
import com.aldhafara.genealogicalTree.models.dto.PersonDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PersonMatcherTest {
    private PersonMatcher matcher;

    @BeforeEach
    void setUp() {
        matcher = new PersonMatcher();
    }

    private PersonDto dto(String firstName, String lastName, String familyName, Character sex, LocalDate birthDate, String birthPlace) {
        PersonDto p = new PersonDto();
        p.setFirstName(firstName);
        p.setLastName(lastName);
        p.setFamilyName(familyName);
        if (sex != null) {
            SexEnum s = switch (sex) {
                case 'M' -> SexEnum.MALE;
                case 'F' -> SexEnum.FEMALE;
                default -> null;
            };
            p.setSex(s);
        }
        p.setBirthDate(birthDate);
        p.setBirthPlace(birthPlace);
        return p;
    }

    private Person entity(String firstName, String lastName, String familyName, Character sex, LocalDate birthDate, String birthPlace) {
        Person p = new Person();
        p.setFirstName(firstName);
        p.setLastName(lastName);
        p.setFamilyName(familyName);
        if (sex != null) {
            SexEnum s = switch (sex) {
                case 'M' -> SexEnum.MALE;
                case 'F' -> SexEnum.FEMALE;
                default -> null;
            };
            p.setSex(s);
        }

        if (birthDate != null) {
            p.setBirthDate(birthDate.atStartOfDay().toInstant(UTC));
        }
        p.setBirthPlace(birthPlace);
        return p;
    }

    @Test
    void shouldReturnPerfectMatchWhenAllFieldsEqual() {
        PersonDto p1 = dto("Jan", "Kowalski", "Nowak", 'M', LocalDate.of(1990, 1, 1), "Warszawa");
        Person p2 = entity("Jan", "Kowalski", "Nowak", 'M', LocalDate.of(1990, 1, 1), "Warszawa");

        double score = matcher.similarityScore(p1, p2);

        assertEquals(1.0, score);
    }

    @Test
    void shouldReturnPartialScoreWhenSomeFieldsDiffer() {
        PersonDto p1 = dto("Jan", "Kowalski", null, 'M', LocalDate.of(1990, 1, 1), "Warszawa");
        Person p2 = entity("Jan", "Nowak", null, 'M', LocalDate.of(1990, 1, 1), "Warszawa");

        double score = matcher.similarityScore(p1, p2);

        assertTrue(score > 0.5);
        assertTrue(score < 1.0);
    }

    @Test
    void shouldReturnZeroWhenNoNameDataProvided() {
        PersonDto p1 = dto(null, null, null, null, null, null);
        Person p2 = entity(null, null, null, null, null, null);

        double score = matcher.similarityScore(p1, p2);

        assertEquals(0.0, score);
    }

    static Stream<TestCase> nullCases() {
        return Stream.of(
                new TestCase(null, new Person()),
                new TestCase(new PersonDto(), null),
                new TestCase(null, null)
        );
    }

    @ParameterizedTest
    @MethodSource("nullCases")
    void shouldReturnZeroWhenAnyPersonIsNull(TestCase tc) {
        double score = matcher.similarityScore(tc.p1, tc.p2);

        assertEquals(0.0, score);
    }

    private record TestCase(PersonDto p1, Person p2) {}

    @Test
    void shouldMatchIgnoringCaseAndMinorTypos() {
        PersonDto p1 = dto("Jan", "Kowalski", null, null, null, null);
        Person p2 = entity("JAN", "Kowaslki", null, null, null, null);

        double score = matcher.similarityScore(p1, p2);

        assertTrue(score > 0.8);
        assertTrue(score < 1.0);
    }

    @Test
    void shouldGivePerfectScoreForBirthDateMatch() {
        PersonDto p1 = dto(null, null, null, null, LocalDate.of(1990, 1, 1), null);
        Person p2 = entity(null, null, null, null, LocalDate.of(1990, 1, 1), null);

        double score = matcher.similarityScore(p1, p2);

        assertEquals(1.0, score);
    }

    @Test
    void shouldReturnPartialScoreWhenOnlyBirthPlaceMatches() {
        PersonDto p1 = dto(null, null, null, null, null, "Warszawa");
        Person p2 = entity(null, null, null, null, null, "Warszawa");

        double score = matcher.similarityScore(p1, p2);

        assertEquals(1.0, score);
    }
}
