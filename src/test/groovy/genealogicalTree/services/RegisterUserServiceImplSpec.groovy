package genealogicalTree.services

import com.aldhafara.genealogicalTree.entities.RegisterUser
import com.aldhafara.genealogicalTree.exceptions.NotUniqueLogin
import com.aldhafara.genealogicalTree.mappers.UserMapper
import com.aldhafara.genealogicalTree.models.UserModel
import com.aldhafara.genealogicalTree.repositories.UserRepository
import com.aldhafara.genealogicalTree.services.RegisterUserServiceImpl
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification
import spock.lang.Subject

class RegisterUserServiceImplSpec extends Specification {

    UserRepository userRepository = Mock()
    PasswordEncoder passwordEncoder = Mock()
    UserMapper userMapper = Mock()

    @Subject
    RegisterUserServiceImpl registerUserService = new RegisterUserServiceImpl(userRepository, passwordEncoder, userMapper)

    def "should save a user successfully when login is unique"() {
        given:
            def userModel = new UserModel(login: "uniqueLOGIN", password: "plainPassword")
            def registerUser = new RegisterUser(login: userModel.login, password: userModel.password)
            def encodedPassword = "encodedPassword"
            def savedUser = new RegisterUser(id: UUID.randomUUID(), login: "uniquelogin", password: encodedPassword, roles: "USER")
            def mappedUserModel = new UserModel(id: savedUser.id, login: savedUser.login, password: savedUser.password, roles: savedUser.roles)

        and:
            userMapper.mapUserModelToRegisterUser(userModel) >> registerUser
            userRepository.existsByLogin("uniquelogin") >> false
            passwordEncoder.encode("plainPassword") >> encodedPassword
            userRepository.save(_ as RegisterUser) >> { args ->
                def saved = args[0]
                saved.id = UUID.randomUUID()
                saved
            }
            userMapper.mapRegisterUserToUserModel(_) >> mappedUserModel

        when:
            def result = registerUserService.save(userModel)

        then:
            1 * userRepository.save({
                it.login == "uniquelogin" &&
                it.password == encodedPassword &&
                it.roles == "USER"
            })

        and:
            result != null
            result.login == mappedUserModel.login
            result.password == mappedUserModel.password
            result.roles == mappedUserModel.roles
    }

    def "should throw NotUniqueLogin when login is not unique"() {
        given:
            def userModel = new UserModel(login: "testUser", password: "plainPassword")
            def registerUser = new RegisterUser(login: userModel.login, password: userModel.password)
        and:
            userMapper.mapUserModelToRegisterUser(userModel) >> registerUser
            userRepository.existsByLogin(userModel.login.toLowerCase()) >> true

        when:
            registerUserService.save(userModel)

        then:
            thrown(NotUniqueLogin)
            0 * userRepository.save(_)
    }

    def "should update user successfully"() {
        given:
            def userModel = new UserModel(id: UUID.randomUUID(), login: "testUser", password: "updatedPassword")
            def registerUser = new RegisterUser(id: userModel.id, login: userModel.login, password: userModel.password)
            def lowerCaseLogin = userModel.login.toLowerCase()
        and:
            userMapper.mapUserModelToRegisterUser(userModel) >> registerUser
            userRepository.save(_) >> registerUser

        when:
            registerUserService.update(userModel)

        then:
            1 * userRepository.save({
                it.login == lowerCaseLogin
                it.roles == "USER"
            })
    }
}
