package genealogicalTree.mappers

import com.aldhafara.genealogicalTree.entities.Family
import com.aldhafara.genealogicalTree.entities.Person
import com.aldhafara.genealogicalTree.mappers.PersonMapper
import com.aldhafara.genealogicalTree.models.PersonModel
import com.aldhafara.genealogicalTree.models.SexEnum
import spock.lang.Specification

import java.time.Clock
import java.time.Instant

import static java.time.ZoneOffset.UTC

class PersonMapperSpec extends Specification {

    PersonMapper personMapper = new PersonMapper()
    Clock clock = Clock.fixed(Instant.parse("2024-01-01T14:00:00Z"), UTC);

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
            def personModel = personMapper.mapPersonToPersonModel(person, null)

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
            def personModel = personMapper.mapPersonToPersonModel(person, null)

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

    def "should map Person to PersonModel with correctly map children field"() {
        given:
            def child1id = UUID.randomUUID()
            def child2id = UUID.randomUUID()
            def child3id = UUID.randomUUID()
            def child1 = new Person(id: child1id, firstName: "John", lastName: "Smith1")
            def child2 = new Person(id: child2id, firstName: "Brian", lastName: "Smith2")
            def child3 = new Person(id: child3id, firstName: "Carl", lastName: "Doe")
            def firstFamily = new Family(children: [child1, child2])
            def secondFamily = new Family(children: [child3])

            def person = new Person(id: UUID.randomUUID(), firstName: "Bruce", lastName: "Smith3")

            def families = [firstFamily, secondFamily]

        when:
            def result = new PersonMapper().mapPersonToPersonModel(person, families)

        then:
            result.children.size() == 3
            result.children.find { it.id == child1id && it.firstName == "John" && it.lastName == "Smith1" }
            result.children.find { it.id == child2id && it.firstName == "Brian" && it.lastName == "Smith2" }
            result.children.find { it.id == child3id && it.firstName == "Carl" && it.lastName == "Doe" }
    }

    def "should map PersonModel and Family to Person  with correct family and children"() {
        given:
            def personId = UUID.randomUUID()
            def addById = UUID.randomUUID()
            def updateDate = Instant.now()

            def personModel = new PersonModel(
                    id: personId,
                    addBy: addById,
                    firstName: "John",
                    lastName: "Doe",
                    updateDate: updateDate,
                    familyName: "Smith",
                    sex: SexEnum.MALE,
                    birthDate: Instant.parse("1980-01-01T00:00:00Z").atZone(UTC).toLocalDate(),
                    birthPlace: "New York"
            )

        and:
            def child1 = new Person(firstName: "Child1")
            def child2 = new Person(firstName: "Child2")
            def child3 = new Person(firstName: "Child3")

            def family = new Family(
                    father: new Person(firstName: "Father"),
                    mother: new Person(firstName: "Mother"),
                    children: [child1, child2, child3]
            )

        when:
            def result = personMapper.mapPersonModelWithFamilyToPerson(personModel, family)

        then:
            result != null
            result.id == personId
            result.addBy == addById
            result.firstName == "John"
            result.lastName == "Doe"
            result.updateDate == updateDate
            result.familyName == "Smith"
            result.sex == SexEnum.MALE
            result.birthDate == Instant.parse("1980-01-01T00:00:00Z")
            result.birthPlace == "New York"
            result.family == family

        and:
            result.family.children.size() == 3
            result.family.children*.firstName == ["Child1", "Child2", "Child3"]
    }
}