package uk.gov.hmcts


import org.jooq.generated.enums.ClaimEvent;
import org.jooq.generated.enums.ClaimState
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import spock.lang.Specification
import uk.gov.hmcts.ccf.api.ApiEventCreation
import uk.gov.hmcts.ccf.controller.claim.ClaimController;
import uk.gov.hmcts.unspec.dto.ConfirmService

@SpringBootTest
@Transactional
class ClaimControllerSpec extends Specification {

    @Autowired
    CaseFactory factory

    @Autowired
    ClaimController controller

    def "A new case has a single claim"() {
        given:
        def response = factory.CreateCase().getBody()
        def claims = controller.getClaims(response.getId())
        ClaimController.Claim claim = claims[0]

        expect: "Case has single claim"
        claims.size() == 1
        claim.claimId > 0
        claim.lowerAmount == 1
        claim.higherAmount == 2
        claim.state == ClaimState.Issued
    }

    def "Confirm service for a claim"() {
        given:
        def userId = factory.createUser()
        def response = factory.CreateCase(userId).getBody()
        def claims = controller.getClaims(response.getId())
        def claim = claims[0]
        ApiEventCreation event = new ApiEventCreation(ClaimEvent.ConfirmService, new ConfirmService("a", "user"));
        controller.createEvent((Long)claim.claimId, event, userId)
        def modifiedClaim = controller.getClaims(response.getId())[0]
        ArrayList claimList = controller.getClaims(response.getId());
        ArrayList history = controller.getClaimEvents(claim.claimId)

        expect:
        modifiedClaim.state == ClaimState.ServiceConfirmed
        claimList.size() == 1
        history.size() == 2
    }

    def "A claim has claimants and defendants"() {
        given:
        def response = factory.CreateCase().getBody()
        def claims = controller.getClaims(response.getId())
        def parties = claims[0].parties
        expect: "Case has single claim"
        parties.defendants.size() == 1
        parties.claimants.size() == 1
        parties.defendants[0].name == 'Wiki'
        parties.claimants[0].name == 'Hooli'
    }
}
