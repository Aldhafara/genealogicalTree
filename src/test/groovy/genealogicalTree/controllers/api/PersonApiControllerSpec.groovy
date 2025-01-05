package genealogicalTree.controllers.api

import com.aldhafara.genealogicalTree.controllers.api.PersonApiController
import com.aldhafara.genealogicalTree.entities.Person
import com.aldhafara.genealogicalTree.exceptions.PersonNotFoundException
import com.aldhafara.genealogicalTree.services.FamilyServiceImpl
import com.aldhafara.genealogicalTree.services.PersonServiceImpl
import spock.lang.Specification
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.boot.test.context.SpringBootTest

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import static com.aldhafara.genealogicalTree.models.SexEnum.FEMALE
import static com.aldhafara.genealogicalTree.models.SexEnum.MALE

@SpringBootTest
class PersonApiControllerSpec extends Specification {

    @Autowired
    MockMvc mockMvc

    PersonApiController personApiController

    def personService
    def familyService

    def setup() {
        personService = Mock(PersonServiceImpl)
        familyService = Mock(FamilyServiceImpl)

        personApiController = new PersonApiController(personService, familyService)

        mockMvc = MockMvcBuilders.standaloneSetup(personApiController)
                .build()
    }

    def "should return found and redirect to saved parent details page when valid parent type #parentType provided"() {
        given:
            UUID personId = UUID.randomUUID()

            def person = new Person(id: personId, sex: MALE)
            def savedPerson = new Person(id: UUID.randomUUID(), sex: FEMALE)
            personService.getById(personId) >> person
            personService.saveParent(_, _) >> savedPerson

        when:
            def result = mockMvc.perform(MockMvcRequestBuilders.get("/api/person/add/parent/$parentType/for/$personId"))

        then:
            result.andExpect(status().isFound())
                    .andExpect(header().string("Location", "/person/${savedPerson.id}"))

        where:
            parentType << ["mother", "father"]
    }

    def "should return bad request when invalid parent type provided"() {
        given:
            UUID personId = UUID.randomUUID()
            String parentType = "invalidType"

        when:
            def result = mockMvc.perform(MockMvcRequestBuilders.get("/api/person/add/parent/$parentType/for/$personId"))

        then:
            result.andExpect(status().isBadRequest())
                    .andExpect(content().string("Invalid parent type"))
    }

    def "should return not found when person not found for adding parent"() {
        given:
            UUID personId = UUID.randomUUID()
            String parentType = "mother"

        and:
            personService.getById(personId) >> { throw new PersonNotFoundException() }

        when:
            def result = mockMvc.perform(MockMvcRequestBuilders.get("/api/person/add/parent/$parentType/for/$personId"))

        then:
            result.andExpect(status().isNotFound())
                    .andExpect(content().string("Person not found"))
    }

    def "should return not found when person not found for adding partner"() {
        given:
            UUID personId = UUID.randomUUID()

        and:
            personService.getById(personId) >> { throw new PersonNotFoundException() }

        when:
            def result = mockMvc.perform(MockMvcRequestBuilders.get("/api/person/add/partner/for/$personId"))

        then:
            result.andExpect(status().isNotFound())
                    .andExpect(content().string("Person not found"))
    }

    def "should return found and redirect to saved sibling details page when valid sibling type #siblingType provided"() {
        given:
            UUID personId = UUID.randomUUID()

            def person = new Person(id: personId, sex: MALE)
            def savedSibling = new Person(id: UUID.randomUUID(), sex: FEMALE)
            personService.getById(personId) >> person
            personService.saveSibling(_, _) >> savedSibling
            familyService.saveChild(_, _) >> {}

        when:
            def result = mockMvc.perform(MockMvcRequestBuilders.get("/api/person/add/sibling/$siblingType/for/$personId"))

        then:
            result.andExpect(status().isFound())
                    .andExpect(header().string("Location", "/person/${savedSibling.id}"))

        where:
            siblingType << ["sister", "brother"]
    }

    def "should return not found when person not found for adding sibling"() {
        given:
            UUID personId = UUID.randomUUID()
            String siblingType = "sister"

        and:
            personService.getById(personId) >> { throw new PersonNotFoundException() }

        when:
            def result = mockMvc.perform(MockMvcRequestBuilders.get("/api/person/add/sibling/$siblingType/for/$personId"))

        then:
            result.andExpect(status().isNotFound())
                    .andExpect(content().string("Person not found"))
    }

    def "should return bad request when invalid sibling type provided"() {
        given:
            UUID personId = UUID.randomUUID()
            String siblingType = "invalidType"

        when:
            def result = mockMvc.perform(MockMvcRequestBuilders.get("/api/person/add/sibling/$siblingType/for/$personId"))

        then:
            result.andExpect(status().isBadRequest())
                    .andExpect(content().string("Invalid sibling type"))
    }

    def "should return found and redirect to saved child details page when valid child type #childType provided"() {
        given:
            UUID firstParentId = UUID.randomUUID()
            UUID secondParentId = UUID.randomUUID()

            def firstParent = new Person(id: firstParentId, sex: MALE)
            def secondParent = new Person(id: secondParentId, sex: FEMALE)
            def savedChild = new Person(id: UUID.randomUUID(), sex: FEMALE)

            personService.getById(firstParentId) >> firstParent
            personService.getById(secondParentId) >> secondParent
            personService.createChildAndSave(_, _, _) >> savedChild
            familyService.saveChild(_, _, _) >> {}

        when:
            def result = mockMvc.perform(MockMvcRequestBuilders.get("/api/person/add/child/$childType/for/$firstParentId/$secondParentId"))

        then:
            result.andExpect(status().isFound())
                    .andExpect(header().string("Location", "/person/${savedChild.id}"))

        where:
            childType << ["daughter", "son"]
    }

    def "should return not found when person not found for adding child"() {
        given:
            UUID firstParentId = UUID.randomUUID()
            UUID secondParentId = UUID.randomUUID()
            String childType = "son"

        and:
            personService.getById(firstParentId) >> { throw new PersonNotFoundException() }

        when:
            def result = mockMvc.perform(MockMvcRequestBuilders.get("/api/person/add/child/$childType/for/$firstParentId/$secondParentId"))

        then:
            result.andExpect(status().isNotFound())
                    .andExpect(content().string("Person not found"))
    }

    def "should return bad request when invalid child type or parents provided"() {
        given:
            UUID firstParentId = UUID.randomUUID()
            UUID secondParentId = UUID.randomUUID()
            String childType = "invalidChild"

        when:
            def result = mockMvc.perform(MockMvcRequestBuilders.get("/api/person/add/child/$childType/for/$firstParentId/$secondParentId"))

        then:
            result.andExpect(status().isBadRequest())
                    .andExpect(content().string("Invalid child type or parents"))
    }
}
