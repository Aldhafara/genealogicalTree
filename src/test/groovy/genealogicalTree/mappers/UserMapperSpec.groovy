package genealogicalTree.mappers

import com.aldhafara.genealogicalTree.entities.RegisterUser
import com.aldhafara.genealogicalTree.mappers.UserMapper
import com.aldhafara.genealogicalTree.models.UserModel
import spock.lang.Specification

class UserMapperSpec extends Specification {

    UserMapper userMapper = new UserMapper()

    def "should map RegisterUser to UserModel"() {
        given:
            RegisterUser registerUser = new RegisterUser(
                id: UUID.fromString("a4be2635-235a-48f5-9be9-2dce4f16d7b4"),
                login: "testUser",
                password: "password123",
                roles: ["USER", "ADMIN"],
                detailsId: UUID.fromString("ae5ba8a8-3e89-4627-b1d4-61c9c8462dc2")
            )

        when:
            UserModel userModel = userMapper.mapRegisterUserToUserModel(registerUser)

        then:
            userModel.id == UUID.fromString("a4be2635-235a-48f5-9be9-2dce4f16d7b4")
            userModel.login == "testUser"
            userModel.password == "password123"
            userModel.roles == "[USER, ADMIN]"
            userModel.detailsId == UUID.fromString("ae5ba8a8-3e89-4627-b1d4-61c9c8462dc2")
    }

    def "should map UserModel to RegisterUser"() {
        given:
            UserModel userModel = new UserModel(
                id: UUID.fromString("a4be2635-235a-48f5-9be9-2dce4f16d7b4"),
                login: "testUser",
                password: "password123",
                roles: ["USER", "ADMIN"],
                detailsId: UUID.fromString("ae5ba8a8-3e89-4627-b1d4-61c9c8462dc2")
            )

        when:
            RegisterUser registerUser = userMapper.mapUserModelToRegisterUser(userModel)

        then:
            registerUser.id == UUID.fromString("a4be2635-235a-48f5-9be9-2dce4f16d7b4")
            registerUser.login == "testUser"
            registerUser.password == "password123"
            registerUser.roles == "[USER, ADMIN]"
            registerUser.detailsId == UUID.fromString("ae5ba8a8-3e89-4627-b1d4-61c9c8462dc2")
    }
}
