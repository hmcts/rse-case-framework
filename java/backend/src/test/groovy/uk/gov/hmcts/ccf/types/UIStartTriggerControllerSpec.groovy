package uk.gov.hmcts.ccf.types

import spock.lang.Specification
import uk.gov.hmcts.ccd.v2.internal.controller.UIStartTriggerController

class UIStartTriggerControllerSpec extends Specification {
    private UIStartTriggerController controller = new UIStartTriggerController()

    def "describes the event"() {
        given:
        def e = controller.getCaseUpdateViewEvent("1", "generalReferral", false).getBody().getCaseUpdateViewEvent()

        expect:
        e.caseFields.size() > 0
        e.wizardPages.size() > 0
    }
}
