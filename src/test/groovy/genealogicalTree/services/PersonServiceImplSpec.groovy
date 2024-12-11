package genealogicalTree.services

import com.aldhafara.genealogicalTree.configuration.SecurityContextFacade
import com.aldhafara.genealogicalTree.entities.Person
import com.aldhafara.genealogicalTree.exceptions.PersonNotFoundException
import com.aldhafara.genealogicalTree.mappers.PersonMapper
import com.aldhafara.genealogicalTree.models.PersonModel
import com.aldhafara.genealogicalTree.repositories.PersonRepository
import com.aldhafara.genealogicalTree.services.PersonServiceImpl
import spock.lang.Specification
import spock.lang.Subject

import java.time.Clock
import java.time.Instant
import java.time.ZoneId

import static com.aldhafara.genealogicalTree.models.SexEnum.FEMALE
import static com.aldhafara.genealogicalTree.models.SexEnum.MALE

class PersonServiceImplSpec extends Specification {

    PersonRepository personRepository = Mock()
    PersonMapper personMapper = Mock()
    SecurityContextFacade securityContextFacade = Mock()
    Clock clock = Clock.fixed(Instant.parse("2024-01-01T12:34:56Z"), ZoneId.of("UTC"))

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
            def personModel = new PersonModel()
            def mappedPerson = new Person()
            def currentUserId = UUID.randomUUID()
            def savedPerson = new Person(id: UUID.randomUUID())
        and:
            personMapper.mapPersonModelToPerson(personModel) >> mappedPerson
            securityContextFacade.getCurrentUserId() >> currentUserId
            personRepository.save(mappedPerson) >> savedPerson

        when:
            def result = personService.saveAndReturnId(personModel)

        then:
            mappedPerson.addBy == currentUserId
        and:
            result == savedPerson.id
    }

    def "should set parent's last name based on person's sex and family name"() {
        given:
            def parentSmithModel = new PersonModel(lastName: "Smith")
            def parentSmith = new Person(lastName: "Smith")
            def parentBrownModel = new PersonModel(lastName: "Brown")
            def parentBrown = new Person(lastName: "Brown")
        and:
            PersonMapper personMapper = new PersonMapper()
            personService = new PersonServiceImpl(personRepository, personMapper, securityContextFacade)
        and:
            personMapper.mapPersonModelToPerson(parentSmithModel) >> parentSmith
            personMapper.mapPersonModelToPerson(parentBrownModel) >> parentBrown
            personRepository.save(_ as Person) >> { Person parent ->
                parent.id = UUID.randomUUID()
                return parent
            }

        when:
            def result = personService.saveParent(new PersonModel(), person)

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
            def siblingSmithModel = new PersonModel(lastName: "Smith")
            def siblingSmith = new Person(lastName: "Smith")
            def siblingBrownModel = new PersonModel(lastName: "Brown")
            def siblingBrown = new Person(lastName: "Brown")
        and:
            PersonMapper personMapper = new PersonMapper()
            PersonServiceImpl personService = new PersonServiceImpl(personRepository, personMapper, securityContextFacade)
        and:
            personMapper.mapPersonModelToPerson(siblingSmithModel) >> siblingSmith
            personMapper.mapPersonModelToPerson(siblingBrownModel) >> siblingBrown
            personRepository.save(_ as Person) >> { Person sibling ->
                sibling.id = UUID.randomUUID()
                return sibling
            }

        when:
            def person = new Person(sex: personSex, lastName: "Smith", familyName: "Brown")
            def result = personService.saveSibling(new PersonModel(sex: siblingSex), person)

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
}