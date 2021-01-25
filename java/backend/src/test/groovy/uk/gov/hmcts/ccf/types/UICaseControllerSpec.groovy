package uk.gov.hmcts.ccf.types

import spock.lang.Specification
import uk.gov.hmcts.ccd.v2.internal.controller.UICaseController

class UICaseControllerSpec extends Specification {
    private UICaseController controller = new UICaseController();

    def "renders the case view"() {
        given:
        def view = controller.getCaseView("1").getBody()

        expect:
        view.getTabs().size() > 0
        view.getTabs()[0].getFields().size() > 0
    }
}
