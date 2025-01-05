package genealogicalTree.controllers.api

import com.aldhafara.genealogicalTree.controllers.api.RegistrationApiController
import com.aldhafara.genealogicalTree.exceptions.NotUniqueLogin
import com.aldhafara.genealogicalTree.models.dto.PersonDto
import com.aldhafara.genealogicalTree.models.dto.UserDto
import com.aldhafara.genealogicalTree.services.RegistrationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@WebMvcTest(RegistrationApiController.class)
class RegistrationApiControllerSpec extends Specification {

    @Autowired
    MockMvc mockMvc

    def registrationService

    def setup() {
        registrationService = Mock(RegistrationService)
        mockMvc = MockMvcBuilders.standaloneSetup(new RegistrationApiController(registrationService))
                .build()
    }

    def "should return registration data with sex options"() {
        when:
            def response = mockMvc.perform(get("/api/register"))
                    .andExpect(status().isOk())
                    .andReturn().response

        then:
            response.contentAsString.contains("sexOptions")
            response.contentAsString.contains("MALE")
            response.contentAsString.contains("FEMALE")
    }

    def "should register a new user successfully"() {
        given:
            def userDto = new UserDto(login: "testUser", password: "password123")
            def personDto = new PersonDto(firstName: "John", lastName: "Doe")
            registrationService.register(userDto, personDto) >> {}

        when:
            def response = mockMvc.perform(post("/api/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonAsString("json/requests/registrationRequest.json")))
                    .andExpect(status().isCreated())
                    .andReturn().response

        then:
            response.contentAsString == "User registered successfully"
    }


    def "should return error when login is not unique"() {
        given:
            registrationService.register(_, _) >> { throw new NotUniqueLogin() }

        when:
            def response = mockMvc.perform(post("/api/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonAsString("json/requests/registrationRequest.json")))
                    .andExpect(status().isBadRequest())
                    .andReturn().response

        then:
            response.status == HttpStatus.BAD_REQUEST.value()
            response.contentAsString == "User login must be unique"
    }

    def jsonAsString(String resourcePath) {
        def resource = this.getClass().getClassLoader().getResource(resourcePath)
        if (resource == null) {
            throw new FileNotFoundException("Resource not found: " + resourcePath)
        }
        return new File(resource.toURI()).text
    }
}