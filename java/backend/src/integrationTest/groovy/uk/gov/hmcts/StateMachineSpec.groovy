package uk.gov.hmcts

import org.jooq.generated.enums.CaseState
import org.jooq.generated.enums.Event
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import spock.lang.Ignore
import spock.lang.Specification
import uk.gov.hmcts.ccf.StateMachine
import uk.gov.hmcts.unspec.event.CreateClaim

class StateMachineSpec extends BaseSpringBootSpec {

//    @Autowired
//    private StateMachine<CaseState, Event> caseSM;

    @Autowired
    private CaseFactory caseFactory;

    @Ignore
    def "a case can be closed"() {
        given:
        def user = caseFactory.createUser()
        def cid = caseFactory.CreateCase(user).getBody().getId()
        def context = StateMachine.TransitionContext.builder()
            .entityId(cid)
            .userId("test")
        CreateClaim c = CreateClaim.builder()
        def data = J
        caseSM.handleEvent(context, Event.CreateCase, data);
    }
}
