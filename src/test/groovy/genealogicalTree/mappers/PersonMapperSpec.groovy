package genealogicalTree.mappers

import com.aldhafara.genealogicalTree.entities.Person
import com.aldhafara.genealogicalTree.mappers.PersonMapper
import com.aldhafara.genealogicalTree.models.PersonModel
import com.aldhafara.genealogicalTree.models.SexEnum
import spock.lang.Specification

import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class PersonMapperSpec extends Specification {

    PersonMapper personMapper = new PersonMapper()
    Clock clock = Clock.fixed(Instant.parse("2024-01-01T14:00:00Z"), ZoneId.of("UTC"));

    def "should map Person to PersonModel correctly"() {
        given:
            def person = Person.builder()
                    .id(UUID.randomUUID())
                    .addBy(UUID.fromString("847c2f40-eda9-4fac-8ba1-3a2c03551934"))
                    .firstName("John")
                    .lastName("Doe")
                    .updateDate(clock.instant())
                    .familyName("Doe Family")
                    .sex(SexEnum.MALE)
                    .birthDate(Instant.parse("1990-06-01T00:00:00Z"))
                    .birthPlace("New York")
                    .build()

        when:
            def personModel = personMapper.mapPersonToPersonModel(person)

        then:
            personModel.id == person.id
            personModel.addBy == person.addBy
            personModel.firstName == person.firstName
            personModel.lastName == person.lastName
            personModel.updateDate == person.updateDate
            personModel.familyName == person.familyName
            personModel.sex == person.sex
            personModel.getBirthDateAsInstant() == person.birthDate
            personModel.birthPlace == person.birthPlace
    }

    def "should map PersonModel to Person correctly"() {
        given:
            def personModel = PersonModel.builder()
                    .id(UUID.randomUUID())
                    .addBy(UUID.fromString("847c2f40-eda9-4fac-8ba1-3a2c03551934"))
                    .firstName("Jane")
                    .lastName("Smith")
                    .updateDate(clock.instant())
                    .familyName("Smith Family")
                    .sex(SexEnum.FEMALE)
                    .setBirthDateFromInstant(Instant.parse("1995-12-15T00:00:00Z"))
                    .birthPlace("Los Angeles")
                    .build()

        when:
            def person = personMapper.mapPersonModelToPerson(personModel)

        then:
            person.id == personModel.id
            person.addBy == personModel.addBy
            person.firstName == personModel.firstName
            person.lastName == personModel.lastName
            person.updateDate == personModel.updateDate
            person.familyName == personModel.familyName
            person.sex == personModel.sex
            person.birthDate == personModel.getBirthDateAsInstant()
            person.birthPlace == personModel.birthPlace
    }

    def "should handle null birthDate in Person when mapping to PersonModel"() {
        given:
            def person = Person.builder()
                    .id(UUID.randomUUID())
                    .addBy(UUID.fromString("847c2f40-eda9-4fac-8ba1-3a2c03551934"))
                    .firstName("Alex")
                    .lastName("Taylor")
                    .birthDate(null)
                    .build()

        when:
            def personModel = personMapper.mapPersonToPersonModel(person)

        then:
            personModel.id == person.id
            personModel.getBirthDateAsInstant() == null
    }

    def "should handle null birthDate in PersonModel when mapping to Person"() {
        given:
            def personModel = PersonModel.builder()
                    .id(UUID.randomUUID())
                    .addBy(UUID.fromString("847c2f40-eda9-4fac-8ba1-3a2c03551934"))
                    .firstName("Emma")
                    .lastName("Brown")
                    .setBirthDateFromInstant(null)
                    .build()

        when:
            def person = personMapper.mapPersonModelToPerson(personModel)

        then:
            person.id == personModel.id
            person.birthDate == null
    }
}