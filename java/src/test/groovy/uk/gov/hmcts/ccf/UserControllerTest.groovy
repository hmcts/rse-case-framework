package uk.gov.hmcts.ccf

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext;
import spock.lang.Specification
import uk.gov.hmcts.ccf.api.UserInfo
import uk.gov.hmcts.ccf.controller.UserController;
import uk.gov.hmcts.unspec.CaseHandlerImpl

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController)
class UserControllerTest extends Specification {
    private MockMvc mockMvc

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserController controller;

    def setup() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();
    }

    @WithMockUser
    def "info of logged in user is provided"() {
        given:
        def json = mockMvc.perform(get("/web/userInfo").with(oidcLogin()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString()
        UserInfo o = new ObjectMapper().readValue(json, UserInfo)

        expect:
        o.getUsername() == 'user'
        o.getRoles() == ['ROLE_USER', 'SCOPE_read'].toSet()
    }
}
