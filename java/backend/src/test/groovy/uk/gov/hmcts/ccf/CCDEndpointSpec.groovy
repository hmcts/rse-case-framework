package uk.gov.hmcts.ccf

import org.jooq.impl.DefaultDSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import spock.lang.Specification
import uk.gov.hmcts.ccd.v2.internal.controller.UIDefinitionController

@WebMvcTest(UIDefinitionController)
class CCDEndpointSpec extends Specification {

    @Autowired
    private UIDefinitionController controller;

    @MockBean
    private DefaultDSLContext jooq;

    // Mock out OIDC client registration.
    @MockBean
    ClientRegistrationRepository registrations;

    def "returns jurisdiction information"() {
        given:
        def jurisdictions = controller.getJurisdictions("read").getBody().jurisdictions
        expect:
        jurisdictions.size() == 1
        jurisdictions[0].caseTypeDefinitions.size() == 1
    }

    def "returns workbasket inputs"() {
        given:
        def inputs = controller.getWorkbasketInputsDetails("NFD").getBody().workbasketInputs
        expect:
        inputs.size() == 1
        inputs[0].label != null
        inputs[0].field != null
    }
}
