package uk.gov.hmcts

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.json.JsonOutput
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification
import uk.gov.hmcts.ccf.api.ApiCase
import uk.gov.hmcts.ccf.api.ApiEventCreation
import uk.gov.hmcts.ccf.api.UserInfo
import uk.gov.hmcts.ccf.controller.CaseController
import uk.gov.hmcts.unspec.dto.Company
import uk.gov.hmcts.unspec.dto.Organisation
import uk.gov.hmcts.unspec.enums.Event
import uk.gov.hmcts.unspec.enums.State
import uk.gov.hmcts.unspec.event.CloseCase
import uk.gov.hmcts.unspec.event.CreateClaim
import uk.gov.hmcts.unspec.event.SubmitAppeal

import javax.sql.DataSource
import java.time.LocalDate

import static org.jooq.generated.Tables.CASES
import static org.jooq.impl.DSL.count
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@Transactional
@Import(TestUsers)
class CaseControllerSpecification extends Specification {

    @Autowired
    private CaseController controller

    @Autowired
    private DataSource dataSource

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc

    def setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

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

    def "A case can be created"() {
        given:
        def response = CreateCase()

        expect: "Status is 201 and the response is the case ID"
        response.getStatusCode() == HttpStatus.CREATED
        response.getHeaders().getLocation().toString().contains("/cases")
    }

    def "a case can be retrieved when logged in"() {
        given:
        def result = CreateCase().getBody()
        def json = mockMvc.perform(get("/web/cases/" + result.getId()).with(oidcLogin()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString()
        ApiCase a = new ObjectMapper().readValue(json, ApiCase.class)

        expect:
        a.getState() == State.Created.toString()
        a.getActions().isEmpty() == false
    }

    def "a case cannot be retrieved when not logged in"() {
        given:
        def result = CreateCase().getBody()
        mockMvc.perform(get("/web/cases/" + result.getId()))
                .andExpect(status().is(401))
    }

    def "an invalid case is not created"() {
        when:
        CreateClaim sol = CreateClaim.builder().defendantReference("@!").claimantReference("@").build()
        JsonNode j = new ObjectMapper().valueToTree(sol)
        controller.createCase(new ApiEventCreation('Create', j), "A", "User")

        then:
        thrown IllegalArgumentException
        caseCount() == 0
    }

    def "A new case has a creation event"() {
        given:
        def response = CreateCase().getBody()
        def events = controller.getCaseEvents(response.getId())
        def event = events.get(0)

        expect: "Case has a single event"
        events.size() == 1
        event.getState() == State.Created.toString()
        LocalDate.now() == event.getTimestamp().toLocalDate()
        event.userForename == "Alex"
        event.userSurname == "M"
    }

    def "An event can change a case's state"() {
        given:
        def response = CreateCase()
        def id = response.getBody().id
        ApiEventCreation event = new ApiEventCreation(Event.CloseCase, new CloseCase("Case withdrawn"))
        controller.createEvent(id, event)

        expect:
        controller.getCase(id).state == State.Closed.toString()
    }

    def "A closed case can be reopened"() {
        given:
        def response = CreateCase()
        def id = response.getBody().id
        ApiEventCreation event = new ApiEventCreation(Event.CloseCase, new CloseCase("Case withdrawn"))
        controller.createEvent(id, event)
        event = new ApiEventCreation(Event.SubmitAppeal, new SubmitAppeal("New evidence"))
        controller.createEvent(id, event)

        expect:
        controller.getCase(id).state == State.Stayed.toString()
    }


    def "search for cases by id"() {
        given:
        CreateCase()
        def c = CreateCase().getBody()
        def query = Map.of("id", c.id)
        def string = Base64.getEncoder().encodeToString(JsonOutput.toJson(query).getBytes())
        def cases = controller.searchCases(string)

        expect:
        cases.size() == 1
        cases[0].id == c.id
        cases[0].state == State.Created.toString()
    }


    private ResponseEntity<ApiCase> CreateCase(name = "a vs b") {
        def event = CreateClaim.builder()
                .claimantReference(name)
                .defendantReference(name)
                .claimant(new Company("Hooli"))
                .defendant(new Organisation("Wiki"))
                .build()
        def request = new ApiEventCreation("Create", new ObjectMapper().valueToTree(event))
        return controller.createCase(request, "A", "User")
    }

    private int caseCount() {
        // Read in a new transaction.
        DSLContext create = DSL.using(dataSource, SQLDialect.DEFAULT);
        return create.select(count()).from(CASES).fetchSingle().value1();
    }
}
