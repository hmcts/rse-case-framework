package uk.gov.hmcts.ccf


import spock.lang.Specification
import uk.gov.hmcts.unspec.CaseHandlerImpl
import uk.gov.hmcts.unspec.enums.Event
import uk.gov.hmcts.unspec.enums.State

class StatemachineTest extends Specification {

    def "no state transition"() {
        given:
        def machine = new CaseHandlerImpl().build()

        when:
        machine.handleEvent(1, Event.SubmitAppeal, null)

        then:
        machine.getState() == State.Created
    }

    def "returns available actions"() {
        given:
        def machine = new CaseHandlerImpl().build()

        when:
        def actions = machine.getAvailableActions(State.Created)

        then:
        actions.size() == 7
    }
}
