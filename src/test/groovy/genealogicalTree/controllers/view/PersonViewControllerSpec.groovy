package genealogicalTree.controllers.view

import com.aldhafara.genealogicalTree.controllers.view.PersonViewController
import com.aldhafara.genealogicalTree.entities.Family
import com.aldhafara.genealogicalTree.entities.Person
import com.aldhafara.genealogicalTree.exceptions.PersonNotFoundException
import com.aldhafara.genealogicalTree.mappers.PersonMapper
import com.aldhafara.genealogicalTree.models.PersonBasicData
import com.aldhafara.genealogicalTree.models.SexEnum
import com.aldhafara.genealogicalTree.models.dto.PersonDto
import com.aldhafara.genealogicalTree.services.FamilyServiceImpl
import com.aldhafara.genealogicalTree.services.person.PersonServiceImpl
import org.springframework.ui.Model
import spock.lang.Specification
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PersonViewControllerSpec extends Specification {

    def personService = Mock(PersonServiceImpl)
    def familyService = Mock(FamilyServiceImpl)
    def personMapper = Mock(PersonMapper)

    def personViewController = new PersonViewController(personService, familyService, personMapper)

    def "should return person details view when person exists"() {
        given:
            UUID personId = UUID.randomUUID()
            Model model = Mock(Model)

            def personDto = new PersonDto(
                    id: personId,
                    siblings: [],
                    children: [],
                    mother: null,
                    father: null,
                    partners: [],
                    familiesAsParent: [],
                    sex: SexEnum.MALE
            )

            personService.getById(personId) >> new Person()
            familyService.getFamiliesWithParent(personId) >> []
            personMapper.mapPersonToPersonDto(_, _) >> personDto

        when:
            def viewName = personViewController.getDetails(personId, model)

        then:
            1 * model.addAttribute("person", personDto)
            1 * model.addAttribute("siblingsWithStepSiblings", personDto.siblingsWithStepSiblings)
            1 * model.addAttribute("siblings", personDto.siblings)
            1 * model.addAttribute("children", personDto.children)
            1 * model.addAttribute("mother", personDto.mother)
            1 * model.addAttribute("father", personDto.father)
            1 * model.addAttribute("partners", personDto.partners)
            1 * model.addAttribute("families", personDto.familiesAsParent)
            1 * model.addAttribute("sexOptions", SexEnum.values())
            viewName == "personDetails"
    }

    def "should return error view when person not found"() {
        given:
            UUID personId = UUID.randomUUID()
            Model model = Mock(Model)

            personService.getById(personId) >> { throw new PersonNotFoundException() }

        when:
            def viewName = personViewController.getDetails(personId, model)

        then:
            1 * model.addAttribute("errorMessage", "Person not found")
            viewName == "error"
    }

    def "should redirect to person details after editing person"() {
        given:
            def personDto = new PersonDto(id: UUID.randomUUID(), familyId: UUID.randomUUID())
            def family = new Family(id: personDto.familyId)
            familyService.getFamilyByIdOrReturnNew(personDto.familyId) >> family
            personService.saveAndReturnId(personDto, family) >> personDto.id

        when:
            def redirectUrl = personViewController.editPerson(personDto)

        then:
            redirectUrl == "redirect:/person/${personDto.id}"
    }

    def "should redirect to person details after adding new person"() {
        given:
            def personDto = new PersonDto()
            UUID newPersonId = UUID.randomUUID()
            personService.saveAndReturnId(_, _) >> newPersonId

        when:
            def redirectUrl = personViewController.addPerson()

        then:
            redirectUrl == "redirect:/person/${newPersonId}"
    }

    def "should return all persons view with data"() {
        given:
            Model model = Mock(Model)
            def allPersons = [
                    new PersonBasicData(UUID.randomUUID(), "John", "Doe"),
                    new PersonBasicData(UUID.randomUUID(), "Jane", "Smith")
            ]
            personService.getAll() >> allPersons

        when:
            def viewName = personViewController.getAllPersons(model)

        then:
            1 * model.addAttribute("allPersons", allPersons)
            viewName == "allPersons"
    }
}