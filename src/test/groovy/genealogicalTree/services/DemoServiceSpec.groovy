package genealogicalTree.services

import com.aldhafara.genealogicalTree.entities.Family
import com.aldhafara.genealogicalTree.entities.Person
import com.aldhafara.genealogicalTree.services.DemoService
import com.aldhafara.genealogicalTree.services.interfaces.FamilyService
import com.aldhafara.genealogicalTree.services.interfaces.PersonService
import spock.lang.Specification
import spock.lang.Subject
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class DemoServiceSpec extends Specification {

    def personService = Mock(PersonService)
    def familyService = Mock(FamilyService)

    @Subject
    def demoService = new DemoService(personService, familyService)

    def "should return existing humperdinkDuck ID if already in database"() {
        given:
            UUID existingId = UUID.randomUUID()
            personService.findIdByFirstNameAndLastName("Abraham", "Kaczor") >> Optional.of(existingId)

        when:
            UUID result = demoService.loadDuckTalesDemo()

        then:
            result == existingId
            0 * personService.save(_)
            0 * familyService.save(_)
    }

    def "should create full DuckTales family tree if humperdinkDuck is missing"() {
        given:
            personService.findIdByFirstNameAndLastName("Abraham", "Kaczor") >> Optional.empty()

            personService.save(_ as Person) >> { Person p -> return p }
            familyService.save(_ as Family) >> { Family f -> return f }

        when:
            demoService.loadDuckTalesDemo()

        then:
            (30.._) * personService.save(_)
            (10.._) * familyService.save(_)
    }
}