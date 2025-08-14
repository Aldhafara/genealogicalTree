package genealogicalTree.services

import com.aldhafara.genealogicalTree.exceptions.NotUniqueLogin
import com.aldhafara.genealogicalTree.models.dto.PersonDto
import com.aldhafara.genealogicalTree.models.dto.UserDto
import com.aldhafara.genealogicalTree.services.RegisterUserServiceImpl
import com.aldhafara.genealogicalTree.services.RegistrationService
import com.aldhafara.genealogicalTree.services.person.PersonServiceImpl
import spock.lang.Specification
import spock.lang.Subject

class RegistrationServiceSpec extends Specification {

    RegisterUserServiceImpl userService = Mock()
    PersonServiceImpl personService = Mock()

    @Subject
    RegistrationService registrationService = new RegistrationService(userService, personService)

    def "should register user and associate person details"() {
        given:
            def user = new UserDto(login: "testUser", password: "password123")
            def person = new PersonDto(firstName: "John", lastName: "Doe")
            def savedUserId = UUID.randomUUID()

        and:
            def savedUser = new UserDto(id: savedUserId, login: "testUser", password: "password123")

        and:
            userService.save(_ as UserDto) >> savedUser
            personService.saveAndReturnId(_ as PersonDto, null) >> UUID.fromString("00000000-0000-0000-0000-000000000001")

        when:
            registrationService.register(user, person)

        then:
            1 * userService.save({ it.login == "testUser" }) >> savedUser

        and:
            1 * personService.saveAndReturnId({ it.addBy == savedUserId }, null) >> UUID.fromString("00000000-0000-0000-0000-000000000001")

        and:
            1 * userService.update({ it.detailsId == UUID.fromString("00000000-0000-0000-0000-000000000001") })
    }

    def "should throw NotUniqueLogin when user service throws it"() {
        given:
            def user = new UserDto(login: "existingUser", password: "password123")
            def person = new PersonDto(firstName: "Jane", lastName: "Doe")

        and:
            userService.save(user) >> { throw new NotUniqueLogin("Login already exists") }

        when:
            registrationService.register(user, person)

        then:
            thrown(NotUniqueLogin)

        and:
            0 * personService._
            0 * userService.update(_)
    }
}
