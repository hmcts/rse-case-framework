package uk.gov.hmcts

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification
import uk.gov.hmcts.ccd.v2.internal.controller.UICaseSearchController
import uk.gov.hmcts.ccd.v2.internal.controller.UIDefinitionController

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class UIDefinitionControllerSpec extends Specification {

    @Autowired
    private CaseFactory factory;

    @Autowired
    private MockMvc mockMvc;

    def "has work basket inputs"() {
        when:
        def f = mockMvc.perform(get('/data/internal/case-types/NFD/work-basket-inputs').with(oidcLogin()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString()
        then:
        f.length() > 0
    }

}
