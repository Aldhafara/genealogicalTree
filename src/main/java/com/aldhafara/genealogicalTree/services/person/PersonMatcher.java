package com.aldhafara.genealogicalTree.services.person;

import com.aldhafara.genealogicalTree.entities.Person;
import com.aldhafara.genealogicalTree.models.dto.PersonDto;
import com.aldhafara.genealogicalTree.models.gedcom.MatchResult;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.function.Supplier;

import static java.time.ZoneOffset.UTC;

@Component
public class PersonMatcher {

    JaroWinklerSimilarity jaroWinkler = new JaroWinklerSimilarity();
    DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public MatchResult similarityScore(PersonDto firstPerson, Person secondPerson) {
        if (firstPerson == null || secondPerson == null) {
            return new MatchResult(0.0, 0);
        }

        AtomicInteger matchedFields = new AtomicInteger();
        AtomicInteger comparedFields = new AtomicInteger();
        DoubleAdder scoreSum = new DoubleAdder();

        compareField(firstPerson::getFirstName, secondPerson::getFirstName, comparedFields, matchedFields, scoreSum);
        compareField(firstPerson::getLastName, secondPerson::getLastName, comparedFields, matchedFields, scoreSum);
        compareField(firstPerson::getFamilyName, secondPerson::getFamilyName, comparedFields, matchedFields, scoreSum);

        compareField(
                () -> firstPerson.getSex() != null ? firstPerson.getSex().getAlternativeName() : null,
                () -> secondPerson.getSex() != null ? secondPerson.getSex().getAlternativeName() : null,
                comparedFields, matchedFields, scoreSum
        );

        compareField(
                () -> firstPerson.getBirthDate() != null ? firstPerson.getBirthDate().format(DATE_FMT) : null,
                () -> secondPerson.getBirthDate() != null ? LocalDate.ofInstant(secondPerson.getBirthDate(), UTC).format(DATE_FMT) : null,
                comparedFields, matchedFields, scoreSum
        );

        compareField(firstPerson::getBirthPlace, secondPerson::getBirthPlace, comparedFields, matchedFields, scoreSum);

        double score = matchedFields.get() == 0 ? 0.0 : scoreSum.doubleValue() / matchedFields.get();
        return new MatchResult(score, matchedFields.get());
    }

    private void compareField(Supplier<String> f1, Supplier<String> f2,
                              AtomicInteger comparedFields, AtomicInteger matchedFields, DoubleAdder scoreSum) {
        String v1 = f1.get();
        String v2 = f2.get();
        if (isFilled(v1) || isFilled(v2)) {
            comparedFields.incrementAndGet();
        }
        if (isFilled(v1) && isFilled(v2)) {
            matchedFields.incrementAndGet();
            scoreSum.add(stringSimilarity(v1, v2));
        }
    }

    private double stringSimilarity(String s1, String s2) {
        return jaroWinkler.apply(s1, s2);
    }

    private boolean isFilled(String s) {
        return s != null && !s.trim().isEmpty();
    }
}