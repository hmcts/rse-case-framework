package uk.gov.hmcts

import org.jooq.impl.DefaultDSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import spock.lang.Specification
import uk.gov.hmcts.ccd.endpoint.ui.QueryEndpoint

@WebMvcTest(QueryEndpoint)
class QueryEndpointSpec extends Specification {

    @Autowired
    private QueryEndpoint query;

    @MockBean
    private DefaultDSLContext jooq;

    // Mock out OIDC client registration.
    @MockBean
    ClientRegistrationRepository registrations;

    def "returns jurisdiction information"() {
        given:
        def jurisdictions = query.getJurisdictions("read")
        expect:
        jurisdictions.size() == 1
        jurisdictions[0].caseTypes.size() == 1
        jurisdictions[0].caseTypes[0].states.size() >= 1
    }
}
