package genealogicalTree.controllers

import com.aldhafara.genealogicalTree.controllers.DefaultController
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.ui.Model
import spock.lang.Specification

class DefaultControllerSpec extends Specification {

    def model = Mock(Model)
    def securityContext = Mock(SecurityContext)
    def authentication = Mock(Authentication)
    def defaultController = new DefaultController()

    def "should return index page for root URL"() {
        when:
            def viewName = defaultController.index()

        then:
            viewName == "index"
    }

    def "should redirect to index after logout"() {
        when:
            def viewName = defaultController.logout()

        then:
            viewName == "redirect:/"
    }

    def "should return homePage and add userName to model"() {
        given:
            securityContext.getAuthentication() >> authentication
            authentication.getName() >> "testUser"

        when:
            def viewName = defaultController.homePage(model, securityContext)

        then:
            1 * model.addAttribute("userName", "testUser")
            viewName == "homePage"
    }

    def "should return login page for login URL"() {
        when:
            def viewName = defaultController.loginPage()

        then:
            viewName == "login"
    }
}
