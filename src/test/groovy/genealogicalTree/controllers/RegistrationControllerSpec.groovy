package genealogicalTree.controllers

import com.aldhafara.genealogicalTree.controllers.RegistrationController
import com.aldhafara.genealogicalTree.exceptions.NotUniqueLogin
import com.aldhafara.genealogicalTree.models.SexEnum
import com.aldhafara.genealogicalTree.models.dto.PersonDto
import com.aldhafara.genealogicalTree.models.dto.UserDto
import com.aldhafara.genealogicalTree.services.PersonServiceImpl
import com.aldhafara.genealogicalTree.services.RegisterUserServiceImpl
import org.springframework.ui.Model
import spock.lang.Specification

import static java.util.UUID.randomUUID

class RegistrationControllerSpec extends Specification {

    def userService = Mock(RegisterUserServiceImpl)
    def personService = Mock(PersonServiceImpl)
    def model = Mock(Model)
    def registrationController = new RegistrationController(userService, personService)

    def "should display registration page"() {
        when:
            def viewName = registrationController.getRegisterPage(model)

        then:
            1 * model.addAttribute("user", _ as UserDto)
            1 * model.addAttribute("person", _ as PersonDto)
            1 * model.addAttribute("sexOptions", SexEnum.values())
            viewName == "register"
    }

    def "should redirect to login page after successful registration"() {
        given:
            def userDto = new UserDto(login: "user", password: "pass")
            def personDto = new PersonDto()
            userService.save(userDto) >> new UserDto(id: randomUUID(), login: userDto.login)
            personService.saveAndReturnId(personDto, null) >> randomUUID()

        when:
            def viewName = registrationController.registerUser(model, userDto, personDto)

        then:
            viewName == "redirect:/login"
            1 * userService.update(_ as UserDto)
    }

    def "should re-display registration page with error message when login is not unique"() {
        given:
            def userDto = new UserDto(login: "user", password: "pass")
            def personDto = new PersonDto()
            userService.save(userDto) >> { throw new NotUniqueLogin() }

        when:
            def viewName = registrationController.registerUser(model, userDto, personDto)

        then:
            1 * model.addAttribute("loginError", "Użytkownik user już istnieje. Zmień login.")
            viewName == "register"
    }
}