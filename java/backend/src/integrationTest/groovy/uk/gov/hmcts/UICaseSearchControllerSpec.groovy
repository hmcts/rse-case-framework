package uk.gov.hmcts

import com.google.common.io.Resources
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification
import uk.gov.hmcts.ccd.v2.internal.controller.UICaseSearchController
import uk.gov.hmcts.ccf.config.WebSecurityConfig

import java.nio.charset.StandardCharsets

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class UICaseSearchControllerSpec extends Specification {

    @Autowired
    private UICaseSearchController controller;

    @Autowired
    private CaseFactory factory;

    @Autowired
    private MockMvc mockMvc;

    def "searches cases"() {
        given:
        URL url = Resources.getResource("requests/data/internal/searchCases.json");
        String json = Resources.toString(url, StandardCharsets.UTF_8);
        factory.CreateCase()
        def result = controller.searchCases("", "", json).getBody()

        expect:
        result.cases.size() > 0
    }

    def "has jurisdictions"() {
        when:
        def f = mockMvc.perform(get('/aggregated/caseworkers/:uid/jurisdictions?access=read').with(oidcLogin()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString()
        then:
        f.length() > 0
    }

}
