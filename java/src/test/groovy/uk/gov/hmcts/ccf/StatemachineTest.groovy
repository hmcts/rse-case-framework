package uk.gov.hmcts.ccf

import org.jooq.generated.enums.CaseState
import spock.lang.Specification
import uk.gov.hmcts.unspec.CaseHandlerImpl
import org.jooq.generated.enums.Event

class StatemachineTest extends Specification {

    def "no state transition"() {
        given:
        def machine = new CaseHandlerImpl().build()

        when:
        machine.handleEvent(1, Event.SubmitAppeal, null)

        then:
        machine.getState() == CaseState.Created
    }
}
