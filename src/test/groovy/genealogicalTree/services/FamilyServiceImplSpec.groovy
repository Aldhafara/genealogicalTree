package genealogicalTree.services

import com.aldhafara.genealogicalTree.configuration.SecurityContextFacade
import com.aldhafara.genealogicalTree.entities.Family
import com.aldhafara.genealogicalTree.entities.Person
import com.aldhafara.genealogicalTree.mappers.FamilyMapper
import com.aldhafara.genealogicalTree.models.FamilyModel
import com.aldhafara.genealogicalTree.repositories.FamilyRepository
import com.aldhafara.genealogicalTree.services.FamilyServiceImpl
import spock.lang.Specification
import spock.lang.Subject

import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class FamilyServiceImplSpec extends Specification {

    FamilyRepository familyRepository = Mock()
    FamilyMapper familyMapper = Mock()
    SecurityContextFacade securityContextFacade = Mock()
    Clock clock = Clock.fixed(Instant.parse("2024-01-01T12:34:56Z"), ZoneId.of("UTC"))

    @Subject
    FamilyServiceImpl familyService = new FamilyServiceImpl(familyRepository, familyMapper, securityContextFacade)

    def setup() {
        familyService.setClock(clock)
    }

    def "should update updateDate when saving family to repository"() {
        given:
            def family = Family.builder()
                    .id(UUID.randomUUID())
                    .addBy(UUID.randomUUID())
                    .father(new Person(id: UUID.randomUUID()))
                    .mother(new Person(id: UUID.randomUUID()))
                    .children([new Person(id: UUID.randomUUID())])
                    .updateDate(null)
                    .build()

        when:
            def result = familyService.save(family)

        then:
            1 * familyRepository.save(_ as Family) >> { args ->
                def savedFamily = args[0]
                assert savedFamily.updateDate == Instant.parse("2024-01-01T12:34:56Z")
                return savedFamily
            }
            result != null
    }


    def "should map FamilyModel to Family and save with addBy with current logged user id"() {
        given:
            def currentUserId = UUID.randomUUID()
            def familyModel = FamilyModel.builder()
                    .id(UUID.randomUUID())
                    .addBy(null)
                    .father(new Person(id: UUID.randomUUID()))
                    .mother(new Person(id: UUID.randomUUID()))
                    .children([new Person(id: UUID.randomUUID())])
                    .updateDate(clock.instant())
                    .build()
            def family = Family.builder()
                    .id(UUID.randomUUID())
                    .addBy(null)
                    .father(new Person(id: UUID.randomUUID()))
                    .mother(new Person(id: UUID.randomUUID()))
                    .children([new Person(id: UUID.randomUUID())])
                    .updateDate(clock.instant())
                    .build()
            securityContextFacade.getCurrentUserId() >> currentUserId
            familyMapper.mapFamilyModelToFamily(familyModel) >> family

        when:
            def result = familyService.save(familyModel)

        then:
            1 * familyRepository.save(_ as Family) >> { args ->
                def savedFamily = args[0]
                assert savedFamily.addBy == currentUserId
                return savedFamily
            }
            result != null
    }

    def "should add child to family and save"() {
        given:
            def family = new Family(children: existingChildren)
            def child = new Person(id: UUID.randomUUID())
        and:
            familyRepository.save(_ as Family) >> { Family savedFamily ->
                savedFamily.id = UUID.randomUUID()
                return savedFamily
            }

        when:
            familyService.save(family, child)

        then:
            family.children.size() == expectedChildrenCount
            family.children.contains(child)

        where:
            existingChildren                    || expectedChildrenCount
            []                                  || 1
            [new Person(id: UUID.randomUUID())] || 2
    }

    def "should throw exception when family is null"() {
        when:
            familyService.save(null, new Person())

        then:
            thrown(NullPointerException)
    }

    def "should handle null child gracefully"() {
        given:
            def family = new Family(children: [])
            def savedFamily = new Family(id: UUID.randomUUID(), children: [])

            familyRepository.save(_) >> savedFamily

        when:
            def result = familyService.save(family, null)

        then:
            family.children.isEmpty()
            result == savedFamily.id
    }
}
