package genealogicalTree.services.gedcom

import com.aldhafara.genealogicalTree.configuration.SecurityContextFacade
import com.aldhafara.genealogicalTree.entities.Person
import com.aldhafara.genealogicalTree.exceptions.PersonNotFoundException
import com.aldhafara.genealogicalTree.mappers.PersonDtoMapper
import com.aldhafara.genealogicalTree.models.dto.PersonDto
import com.aldhafara.genealogicalTree.services.gedcom.GedcomImportService
import com.aldhafara.genealogicalTree.services.person.PersonMatcher
import com.aldhafara.genealogicalTree.services.person.PersonServiceImpl
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class GedcomImportServiceSpec extends Specification {

    PersonServiceImpl personService = Mock()
    PersonDtoMapper personDtoMapper = Mock()
    SecurityContextFacade securityContextFacade = Mock()
    PersonMatcher personMatcher = Mock()

    @Subject
    GedcomImportService service = new GedcomImportService(personService, personDtoMapper, securityContextFacade, personMatcher)

    @Ignore
    def "should map GEDCOM JSON, calculate similarity and save when best match >= 0.9"() {
        given:
            def uuid = UUID.randomUUID()
            def json = '{"name":{"givn":"John"}}'
            def personDto = new PersonDto()
            def loggedPerson = new Person(id: uuid)

            personDtoMapper.mapJsonPersonToPerson(_) >> { args ->
                println "STUB CALLED! arg type=${args[0]?.class?.name}, value=${args[0]}"
                return personDto
            }
            securityContextFacade.getCurrentUserDetailsId() >> uuid
            personService.getById(uuid) >> loggedPerson
            personMatcher.similarityScore(_ as PersonDto, _ as Person) >> 0.95
            personService.saveAndReturn(_ as PersonDto) >> new Person()

        when:
            service.importFromGedcom([json])

        then:
            1 * personDtoMapper.mapJsonPersonToPerson(_)
            1 * personMatcher.similarityScore(_ as PersonDto, _ as Person)
            1 * personService.saveAndReturn(personDto)
    }


    def "should not save any persons when top match is < 0.9"() {
        given:
            def gedcomJson = '{"name":{"givn":"Jane","surn":"Doe"}}'
            def dto = new PersonDto()
            def loggedPerson = new Person()

            personDtoMapper.mapJsonPersonToPerson(_) >> dto
            securityContextFacade.getCurrentUserDetailsId() >> UUID.randomUUID()
            personService.getById(_) >> loggedPerson
            personMatcher.similarityScore(dto, loggedPerson) >> 0.7

        when:
            service.importFromGedcom([gedcomJson])

        then:
            0 * personService.saveAndReturn(_)
    }

    def "should throw PersonNotFoundException when logged person not found"() {
        given:
            securityContextFacade.getCurrentUserDetailsId() >> UUID.randomUUID()
            personService.getById(_) >> { throw new PersonNotFoundException() }

        when:
            service.importFromGedcom(["{}"])

        then:
            thrown(PersonNotFoundException)
    }

    def "should sort persons by similarity descending before saving"() {
        given:
            def uuid = UUID.fromString("cafe1111-cafe-4000-8000-123456789abc")
            def dto1 = new PersonDto()
            def dto2 = new PersonDto()
            def loggedPerson = new Person()

        and:
            personDtoMapper.mapJsonPersonToPerson(_) >>> [dto1, dto2]
            securityContextFacade.getCurrentUserDetailsId() >> uuid
            personService.getById(_) >> loggedPerson
            personMatcher.similarityScore(dto1, loggedPerson) >> 0.91
            personMatcher.similarityScore(dto2, loggedPerson) >> 0.95
            personService.saveAndReturn(_) >> new Person()

        when:
            service.importFromGedcom(["{}", "{}"])

        then:
            1 * personService.saveAndReturn(dto1)
            1 * personService.saveAndReturn(dto2)
    }

    @Unroll
    def "should save person when similarity above or equal threshold [similarity=#similarity]"() {
        given:
            def gedcomJson = '{"name":{"givn":"Test"}}'
            def dto = new PersonDto()
            def loggedPerson = new Person()

            personDtoMapper.mapJsonPersonToPerson(_) >> dto
            securityContextFacade.getCurrentUserDetailsId() >> UUID.randomUUID()
            personService.getById(_) >> loggedPerson
            personMatcher.similarityScore(_, _) >> similarity

        when:
            service.importFromGedcom([gedcomJson])

        then:
            (shouldSave ? 1 : 0) * personService.saveAndReturn(dto)

        where:
            similarity || shouldSave
            0.89       || false
            0.90       || true
            0.95       || true
    }
}
