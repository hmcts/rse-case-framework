package uk.gov.hmcts


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification
import uk.gov.hmcts.ccf.controller.CitizenController

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@Transactional
class CitizenControllerSpec extends Specification {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    CaseFactory factory;

    // Mock out OIDC client registration.
    @MockBean
    ClientRegistrationRepository registrations;

    private MockMvc mockMvc

    def setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Autowired
    CitizenController controller;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @WithMockUser
    def "Imports the csv"(){
        given:
        def caseId = factory.CreateCase().getBody().id
        String csv = this.getClass().getResource('/citizens.csv').text
        when:
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "hello.txt",
                MediaType.TEXT_PLAIN_VALUE,
                csv.getBytes())

        then:
        mockMvc.perform(multipart("/web/cases/" + caseId + "/files")
                .file(file).with(oidcLogin()).with(csrf()))
                .andExpect(status().is(201));
    }
}
