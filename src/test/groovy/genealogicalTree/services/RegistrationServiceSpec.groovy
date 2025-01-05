package genealogicalTree.services

import com.aldhafara.genealogicalTree.exceptions.NotUniqueLogin
import com.aldhafara.genealogicalTree.models.dto.PersonDto
import com.aldhafara.genealogicalTree.models.dto.UserDto
import com.aldhafara.genealogicalTree.services.PersonServiceImpl
import com.aldhafara.genealogicalTree.services.RegisterUserServiceImpl
import com.aldhafara.genealogicalTree.services.RegistrationService
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

        and:
            def savedPersonId = UUID.randomUUID()
            def savedUser = new UserDto(id: savedPersonId, login: user.getLogin(), password: user.getPassword())

        and:
            userService.save(user) >> savedUser
            personService.saveAndReturnId(person, null) >> savedPersonId

        when:
            registrationService.register(user, person)

        then:
            1 * userService.save(user)

        and:
            1 * personService.saveAndReturnId({ it.addBy == savedPersonId }, null)

        and:
            1 * userService.update({ it.detailsId == savedPersonId })
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
