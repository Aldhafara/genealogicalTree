package genealogicalTree.mappers

import com.aldhafara.genealogicalTree.mappers.PersonDtoMapper
import com.aldhafara.genealogicalTree.models.SexEnum
import com.aldhafara.genealogicalTree.models.dto.PersonDto
import com.aldhafara.genealogicalTree.models.gedcom.ParsedDate
import com.aldhafara.genealogicalTree.models.gedcom.Precision
import spock.lang.Specification

class PersonDtoMapperSpec extends Specification {

    def mapper = new PersonDtoMapper()

    def "parseDate returns FULL precision for full date"() {
        when:
            ParsedDate result = mapper.parseDate("1 JAN 1990")

        then:
            result.instant() != null
            result.precision() == Precision.FULL
    }

    def "parseDate returns MONTH precision for month and year"() {
        when:
            ParsedDate result = mapper.parseDate("JAN 1990")

        then:
            result.instant() != null
            result.precision() == Precision.MONTH
    }

    def "parseDate returns YEAR precision for year only"() {
        when:
            ParsedDate result = mapper.parseDate("1990")

        then:
            result.instant() != null
            result.precision() == Precision.YEAR
    }

    def "parseDate returns DECADE precision for decade only"() {
        when:
            ParsedDate result = mapper.parseDate("199*")

        then:
            result.instant() != null
            result.precision() == Precision.DECADE
            result.note() == "Only decade known"
    }

    def "parseDate handles approximate dates like ABT, BEF, AFT"() {
        when:
            ParsedDate result = mapper.parseDate(input)

        then:
            result.instant() != null
            result.precision() in [Precision.YEAR, Precision.MONTH, Precision.FULL]
            result.note() == "Approximate"

        where:
            input << ["ABT 1990", "BEF JAN 1990", "AFT 1 JAN 1990"]
    }

    def "parseDate returns UNKNOWN for date ranges"() {
        when:
            ParsedDate result = mapper.parseDate("FROM 1900 TO 1920")

        then:
            result.instant() == null
            result.precision() == Precision.UNKNOWN
            result.note() == "Date range not supported"
    }

    def "parseDate returns UNKNOWN for invalid date format"() {
        when:
            ParsedDate result = mapper.parseDate("some weird text")

        then:
            result.instant() == null
            result.precision() == Precision.UNKNOWN
            result.note() == "Unrecognized format"
    }

    def "parseDate returns UNKNOWN for null input"() {
        when:
            ParsedDate result = mapper.parseDate(null)

        then:
            result.instant() == null
            result.precision() == Precision.UNKNOWN
            result.note() == "Date is null"
    }

    def "mapJsonPersonToPerson parses valid JSON correctly"() {
        given:
            String json = '''
            {
                "name" : {
                    "value" : "John /Smith/",
                    "givn" : "John",
                    "surn" : "Smith"
                },
                "sex" : "M",
                "birt" : {
                    "value" : "",
                    "date" : "1 JAN 1990",
                    "plac" : "Warsaw"
                }
            }
            '''

        when:
            PersonDto person = mapper.mapJsonPersonToPerson(json)

        then:
            person.getFirstName() == "John"
            person.getLastName() == "Smith"
            person.getFamilyName() == null
            person.getSex() == SexEnum.MALE
            person.getBirthPlace() == "Warsaw"
            person.getBirthDate() != null
    }

    def "mapJsonPersonToPerson throws exception on invalid JSON"() {
        given:
            String invalidJson = "{ invalid : json }"

        when:
            mapper.mapJsonPersonToPerson(invalidJson)

        then:
            thrown(IllegalArgumentException)
    }
}
