package com.aldhafara.genealogicalTree.mappers;

import com.aldhafara.genealogicalTree.models.SexEnum;
import com.aldhafara.genealogicalTree.models.dto.PersonDto;
import com.aldhafara.genealogicalTree.models.gedcom.ParsedDate;
import com.aldhafara.genealogicalTree.models.gedcom.PersonGedDto;
import com.aldhafara.genealogicalTree.models.gedcom.Precision;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static java.time.ZoneOffset.UTC;
import static java.util.Locale.ENGLISH;

@Component
public class PersonDtoMapper {

    private static final Logger logger = Logger.getLogger(PersonDtoMapper.class.getName());

    private static final DateTimeFormatter FULL_FORMAT = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("d MMM uuuu")
            .toFormatter(ENGLISH);
    private static final DateTimeFormatter MONTH_YEAR_FORMAT = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("MMM uuuu")
            .toFormatter(ENGLISH);
    private static final DateTimeFormatter YEAR_FORMAT = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("uuuu")
            .toFormatter(ENGLISH);
    private static final Pattern FULL_DATE = Pattern.compile("\\d{1,2} [A-Z]{3} \\d{4}");
    private static final Pattern MONTH_YEAR = Pattern.compile("[A-Z]{3} \\d{4}");
    private static final Pattern YEAR = Pattern.compile("\\d{4}");
    private static final Pattern DECADE = Pattern.compile("\\d{3}\\*");

    private final ObjectMapper objectMapper = new ObjectMapper();

    public PersonDto mapJsonPersonToPerson(String gedcomPersonJson) {
        try {
            PersonGedDto dto = objectMapper.readValue(gedcomPersonJson, PersonGedDto.class);

            var name = dto.getName();
            String given = name != null ? name.getGiven() : null;
            String marriage = name != null ? name.getMarrigeName() : null;
            String last = name != null ? name.getLastName() : null;
            String lastName = marriage == null ? last : marriage;
            String familyName = marriage == null ? null : last;

            var birth = dto.getBirt();
            ParsedDate birthDate = birth != null ? parseDate(birth.getDate()) : null;

            return PersonDto.builder()
                    .firstName(given)
                    .lastName(lastName)
                    .familyName(familyName)
                    .sex(SexEnum.fromChar(dto.getSex()))
                    .birthPlace(birth != null ? birth.getLocation() : null)
                    .setBirthDateFromInstant(birthDate != null ? birthDate.instant() : null)
                    .build();

        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON: " + e.getMessage(), e);
        }
    }

    public ParsedDate parseDate(String rawDate) {
        if (rawDate == null) return new ParsedDate(null, Precision.UNKNOWN, null, "Date is null");
        String input = rawDate.toUpperCase().replaceAll("[\\p{Zs}\\s]+", " ").trim();

        try {
            if (input.matches("FROM .* TO .*"))
                return new ParsedDate(null, Precision.UNKNOWN, rawDate, "Date range not supported");

            if (input.matches("ABT .*") || input.matches("BEF .*") || input.matches("AFT .*"))
                return handleApproximate(input, rawDate);

            if (FULL_DATE.matcher(input).matches())
                return new ParsedDate(LocalDate.parse(input, FULL_FORMAT).atStartOfDay(UTC).toInstant(), Precision.FULL, rawDate, null);
            if (MONTH_YEAR.matcher(input).matches())
                return new ParsedDate(YearMonth.parse(input, MONTH_YEAR_FORMAT).atDay(1).atStartOfDay(UTC).toInstant(), Precision.MONTH, rawDate, null);
            if (YEAR.matcher(input).matches())
                return new ParsedDate(Year.parse(input, YEAR_FORMAT).atDay(1).atStartOfDay(UTC).toInstant(), Precision.YEAR, rawDate, null);
            if (DECADE.matcher(input).matches()) {
                String decadeStart = input.replace("*", "0");
                Year year = Year.of(Integer.parseInt(decadeStart));
                return new ParsedDate(year.atDay(1).atStartOfDay(UTC).toInstant(), Precision.DECADE, rawDate, "Only decade known");
            }
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }
        return new ParsedDate(null, Precision.UNKNOWN, rawDate, "Unrecognized format");
    }

    private ParsedDate handleApproximate(String input, String raw) {
        String cleaned = input.replaceFirst("ABT |BEF |AFT ", "").trim();
        ParsedDate parsed = parseDate(cleaned);
        return new ParsedDate(parsed.instant(), parsed.precision(), raw, "Approximate");
    }
}
