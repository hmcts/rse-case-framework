package uk.gov.hmcts

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification
import uk.gov.hmcts.ccd.v2.internal.controller.UICaseSearchController

@Transactional
@SpringBootTest
class UICaseSearchControllerSpec extends Specification {

    @Autowired
    private UICaseSearchController controller;

    @Autowired
    private CaseFactory factory;

    def "searches cases"() {
        given:
        factory.CreateCase()
        def result = controller.searchCases("", "", "").getBody()

        expect:
        result.cases.size() > 0
    }
}
