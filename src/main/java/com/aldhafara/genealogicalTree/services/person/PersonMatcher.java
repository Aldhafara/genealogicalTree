package com.aldhafara.genealogicalTree.services.person;

import com.aldhafara.genealogicalTree.entities.Person;
import com.aldhafara.genealogicalTree.models.dto.PersonDto;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.function.Supplier;

import static java.time.ZoneOffset.UTC;

@Component
public class PersonMatcher {

    private final LevenshteinDistance levenshtein = new LevenshteinDistance();

    public double similarityScore(PersonDto p1, Person p2) {
        AtomicInteger total = new AtomicInteger();
        DoubleAdder score = new DoubleAdder();

        compareField(p1::getFirstName, p2::getFirstName, total, score);
        compareField(p1::getLastName, p2::getLastName, total, score);
        compareField(p1::getFamilyName, p2::getFamilyName, total, score);

        compareField(() -> p1.getSex() != null ? p1.getSex().getAlternativeName() : null,
                () -> p2.getSex() != null ? p2.getSex().getAlternativeName() : null,
                total, score);

        if (p1.getBirthDate() != null && p2.getBirthDate() != null &&
                p1.getBirthDate().equals(LocalDate.ofInstant(p2.getBirthDate(), UTC))) {
            total.incrementAndGet();
            score.add(1.0);
        }

        compareField(p1::getBirthPlace, p2::getBirthPlace, total, score);

        return total.get() == 0 ? 0.0 : score.doubleValue() / total.get();
    }

    private void compareField(Supplier<String> f1, Supplier<String> f2,
                              AtomicInteger total, DoubleAdder score) {
        String v1 = f1.get();
        String v2 = f2.get();
        if (isFilled(v1) && isFilled(v2)) {
            total.incrementAndGet();
            score.add(stringSimilarity(v1, v2));
        }
    }

    private double stringSimilarity(String s1, String s2) {
        int distance = levenshtein.apply(s1.toLowerCase(), s2.toLowerCase());
        int maxLength = Math.max(s1.length(), s2.length());
        return maxLength == 0 ? 1.0 : 1.0 - ((double) distance / maxLength);
    }

    private boolean isFilled(String s) {
        return s != null && !s.trim().isEmpty();
    }
}