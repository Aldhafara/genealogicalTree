package com.aldhafara.genealogicalTree.controllers.api

import com.aldhafara.genealogicalTree.exceptions.NotUniqueLogin
import com.aldhafara.genealogicalTree.models.dto.PersonDto
import com.aldhafara.genealogicalTree.models.dto.UserDto
import com.aldhafara.genealogicalTree.services.RegistrationService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class RegistrationApiControllerSpec extends Specification {

    MockMvc mockMvc
    RegistrationService registrationService = Mock()

    def setup() {
        def controller = new RegistrationApiController(registrationService)

        def objectMapper = new ObjectMapper()
        objectMapper.findAndRegisterModules()

        def validator = new LocalValidatorFactoryBean()
        validator.afterPropertiesSet()

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(
                        new StringHttpMessageConverter(),
                        new MappingJackson2HttpMessageConverter(objectMapper)
                )
                .setValidator(validator)
                .build()
    }

    def "should register user"() {
        when:
            def result = mockMvc.perform(post("/api/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(validJson))

        then:
            1 * registrationService.register(
                    { UserDto user ->
                        user.login == "existingUser" &&
                                user.password == "password123"
                    },
                    { PersonDto person ->
                        person.firstName == "John" &&
                                person.lastName == "Doe" &&
                                person.sex != null
                    }
            )

            result.andExpect(status().isCreated())
                    .andExpect(content().string("User registered successfully"))
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

    def "should return error when login is not unique"() {
        when:
            def result = mockMvc.perform(post("/api/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(validJson))

        then:
            1 * registrationService.register(_ as UserDto, _ as PersonDto) >> {
                throw new NotUniqueLogin("User login must be unique")
            }

            result.andExpect(status().isBadRequest())
                    .andExpect(content().string("User login must be unique"))
    }

    def "should return bad request when request is invalid"() {
        given:
            def invalidJson = '''
        {
          "registerUser": {
            "login": "",
            "password": "123"
          },
          "personDetails": {
            "firstName": "",
            "lastName": "D",
            "sex": null
          }
        }
        '''

        when:
            def result = mockMvc.perform(post("/api/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidJson))

        then:
            result.andExpect(status().isBadRequest())
            0 * registrationService.register(_, _)
    }

    def validJson = '''
        {
          "registerUser": {
            "login": "existingUser",
            "password": "password123"
          },
          "personDetails": {
            "firstName": "John",
            "lastName": "Doe",
            "sex": "MALE"
          }
        }
        '''
}
