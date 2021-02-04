package uk.gov.hmcts


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification
import uk.gov.hmcts.ccd.v2.internal.controller.UIStartTriggerController
import uk.gov.hmcts.unspec.dto.AddClaim

@Transactional
@SpringBootTest
class UIStartTriggerControllerSpec extends Specification {

    @Autowired
    private CaseFactory factory

    @Autowired
    private UIStartTriggerController controller

    def "has dynamic events"() {
        given:
        def c = factory.CreateCase(factory.createUser("1"))
        def u = controller.getCaseUpdateViewEvent(c.getBody().getId().toString(),
                "AddClaim", false).getBody()

        expect:
        u.getCaseUpdateViewEvent().caseFields.size() == 4
        u.getCaseUpdateViewEvent().caseFields[2].getFieldTypeDefinition().getFixedListItemDefinitions().size() == 2
    }
}
