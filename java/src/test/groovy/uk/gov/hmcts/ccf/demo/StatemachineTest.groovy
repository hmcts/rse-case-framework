package uk.gov.hmcts.ccf.demo


import spock.lang.Specification
import uk.gov.hmcts.ccf.demo.enums.Event
import uk.gov.hmcts.ccf.demo.enums.State

class StatemachineTest extends Specification {

    def "no state transition"() {
        given:
        def machine = new StatemachineConfig().build()

        when:
        machine.handleEvent(1, Event.SubmitAppeal, null)

        then:
        machine.getState() == State.Created
    }

    def "returns available actions"() {
        given:
        def machine = new StatemachineConfig().build()

        when:
        def actions = machine.getAvailableActions(State.Created)

        then:
        actions.size() == 2
    }
}
