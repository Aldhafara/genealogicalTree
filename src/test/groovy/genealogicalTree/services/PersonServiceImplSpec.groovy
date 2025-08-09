package genealogicalTree.services

import com.aldhafara.genealogicalTree.configuration.SecurityContextFacade
import com.aldhafara.genealogicalTree.entities.Person
import com.aldhafara.genealogicalTree.exceptions.PersonNotFoundException
import com.aldhafara.genealogicalTree.mappers.FamilyMapper
import com.aldhafara.genealogicalTree.mappers.PersonMapper
import com.aldhafara.genealogicalTree.models.dto.PersonDto
import com.aldhafara.genealogicalTree.repositories.PersonRepository
import com.aldhafara.genealogicalTree.services.person.PersonServiceImpl
import jakarta.transaction.Transactional
import org.springframework.test.annotation.Rollback
import spock.lang.Specification
import spock.lang.Subject

import java.time.Clock
import java.time.Instant

import static com.aldhafara.genealogicalTree.models.SexEnum.FEMALE
import static com.aldhafara.genealogicalTree.models.SexEnum.MALE
import static java.time.Clock.fixed
import static java.time.ZoneOffset.UTC

@Rollback
@Transactional
class PersonServiceImplSpec extends Specification {

    PersonRepository personRepository = Mock()
    PersonMapper personMapper = Mock()
    FamilyMapper familyMapper = Mock()
    SecurityContextFacade securityContextFacade = Mock()
    Clock clock = fixed(Instant.parse("2024-01-01T12:34:56Z"), UTC)

    @Subject
    PersonServiceImpl personService = new PersonServiceImpl(personRepository, personMapper, securityContextFacade)

    def setup() {
        personService.setClock(clock)
    }

    def "should save person and set update date"() {
        given:
            def person = new Person()

        when:
            def result = personService.save(person)

        then:
            1 * personRepository.save(_ as Person) >> { args ->
                def savedPerson = args[0]
                assert savedPerson.updateDate == Instant.parse("2024-01-01T12:34:56Z")
                return savedPerson
            }
            result != null
    }

    def "should rollback transaction if savePerson fails"() {
        given:
            def personModel = new PersonDto(firstName: "John", lastName: "Doe")
            def mappedPerson = new Person(firstName: "John", lastName: "Doe")
            personMapper.mapPersonDtoWithFamilyToPerson(personModel, null) >> mappedPerson
            personRepository.save(_) >> { throw new RuntimeException("Database error") }

        when:
            personService.saveAndReturnPerson(personModel, null)

        then:
            thrown(RuntimeException)
    }

    def "should throw exception when person not found by ID"() {
        given:
            def id = UUID.randomUUID()
        and:
            personRepository.findById(id) >> Optional.empty()

        when:
            personService.getById(id)

        then:
            thrown(PersonNotFoundException)
    }

    def "should return person when found by ID"() {
        given:
            def id = UUID.randomUUID()
            def person = new Person(id: id)
        and:
            personRepository.findById(id) >> Optional.of(person)

        when:
            def result = personService.getById(id)

        then:
            result == person
    }

    def "should map PersonModel to Person and save with current user as addBy"() {
        given:
            def personModel = new PersonDto()
            def mappedPerson = new Person()
            def currentUserId = UUID.randomUUID()
            def savedPerson = new Person(id: UUID.randomUUID())
        and:
            personMapper.mapPersonDtoWithFamilyToPerson(personModel,null) >> mappedPerson
            securityContextFacade.getCurrentUserId() >> currentUserId
            personRepository.save(mappedPerson) >> savedPerson

        when:
            def result = personService.saveAndReturnId(personModel, null)

        then:
            mappedPerson.addBy == currentUserId
        and:
            result == savedPerson.id
    }

    def "should set parent's last name based on person's sex and family name"() {
        given:
            def parentSmithModel = new PersonDto(lastName: "Smith")
            def parentSmith = new Person(lastName: "Smith")
            def parentBrownModel = new PersonDto(lastName: "Brown")
            def parentBrown = new Person(lastName: "Brown")
        and:
            PersonMapper personMapper = new PersonMapper(familyMapper)
            personService = new PersonServiceImpl(personRepository, personMapper, securityContextFacade)
        and:
            personMapper.mapPersonDtoToPerson(parentSmithModel) >> parentSmith
            personMapper.mapPersonDtoToPerson(parentBrownModel) >> parentBrown
            personRepository.save(_ as Person) >> { Person parent ->
                parent.id = UUID.randomUUID()
                return parent
            }

        when:
            def result = personService.saveParent(new PersonDto(), person)

        then:
            result.lastName == expectedParentLastName

        where:
            person                                                          || expectedParentLastName
            new Person(sex: MALE, lastName: "Smith", familyName: "Brown")   || "Smith"
            new Person(sex: FEMALE, lastName: "Smith")                      || "Smith"
            new Person(sex: FEMALE, lastName: "Smith", familyName: "Brown") || "Brown"
    }

    def "should save sibling with adjusted family name based on person's sex"() {
        given:
            def siblingSmithModel = new PersonDto(lastName: "Smith")
            def siblingSmith = new Person(lastName: "Smith")
            def siblingBrownModel = new PersonDto(lastName: "Brown")
            def siblingBrown = new Person(lastName: "Brown")
        and:
            PersonMapper personMapper = new PersonMapper(familyMapper)
            PersonServiceImpl personService = new PersonServiceImpl(personRepository, personMapper, securityContextFacade)
        and:
            personMapper.mapPersonDtoToPerson(siblingSmithModel) >> siblingSmith
            personMapper.mapPersonDtoToPerson(siblingBrownModel) >> siblingBrown
            personRepository.save(_ as Person) >> { Person sibling ->
                sibling.id = UUID.randomUUID()
                return sibling
            }

        when:
            def person = new Person(sex: personSex, lastName: "Smith", familyName: "Brown")
            def result = personService.saveSibling(new PersonDto(sex: siblingSex), person)

        then:
            result.lastName == expectedLastName
            result.familyName == expectedFamilyName

        where:
            siblingSex | personSex  || expectedLastName | expectedFamilyName
            MALE       | MALE       || "Smith"          | null
            MALE       | FEMALE     || "Brown"          | null
            FEMALE     | MALE       || null             | "Smith"
            FEMALE     | FEMALE     || null             | "Brown"
    }

    def "createChildAndSave should assign proper last name to child"() {
        given:
            def childModel = new PersonDto()

        when:
            personService.createChildAndSave(childModel, father, mother)

        then:
            childModel.lastName == expectedLastName

        where:
            father                                   | mother                                                              || expectedLastName
            new Person(sex: MALE, lastName: "Smith") | new Person(sex: FEMALE, lastName: "Brown", familyName: "Forrester") || "Smith"
            new Person(sex: MALE, lastName: "")      | new Person(sex: FEMALE, lastName: "Brown", familyName: "Forrester") || "Brown"
            new Person(sex: MALE)                    | new Person(sex: FEMALE, lastName: "Brown", familyName: "Forrester") || "Brown"
            new Person(sex: MALE)                    | new Person(sex: FEMALE, lastName: "", familyName: "Forrester")      || "Forrester"
            new Person(sex: MALE)                    | new Person(sex: FEMALE, familyName: "Forrester")                    || "Forrester"
            new Person(sex: MALE)                    | new Person(sex: FEMALE, familyName: "")                             || null
            new Person(sex: MALE)                    | new Person(sex: FEMALE)                                             || null
    }
}
