package uk.gov.hmcts;

import groovy.json.JsonSlurper;
import org.jooq.generated.enums.ClaimState
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import spock.lang.Specification
import uk.gov.hmcts.ccf.TransitionContext
import uk.gov.hmcts.ccf.controller.ClaimController;
import uk.gov.hmcts.unspec.dto.ConfirmService;

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
        def claims = new JsonSlurper().parseText(controller.getClaims(response.getId()))
        def claim = claims[0]

        expect: "Case has single claim"
        claims.size() == 1
        claim.claim_id > 0
        claim.lower_amount == 1
        claim.higher_amount == 2
        claim.state == 'Issued'
    }

    def "Confirm service for a claim"() {
        given:
        def userId = factory.createUser()
        def response = factory.CreateCase(userId).getBody()
        def claims = new JsonSlurper().parseText(controller.getClaims(response.getId()))
        def claim = claims[0]
        def service = ConfirmService.builder().build();
        TransitionContext context = new TransitionContext(userId, claim.claim_id)
        controller.confirmService(context, service)
        def modifiedClaim = new JsonSlurper().parseText(controller.getClaims(response.getId()))[0]

        expect:
        modifiedClaim.state == ClaimState.ServiceConfirmed.toString()
    }

    def "A claim has claimants and defendants"() {
        given:
        def response = factory.CreateCase().getBody()
        def claims = new JsonSlurper().parseText(controller.getClaims(response.getId()))
        def parties = claims[0].parties
        expect: "Case has single claim"
        parties.defendants.size() == 1
        parties.claimants.size() == 1
        parties.defendants[0].name == 'Wiki'
        parties.claimants[0].name == 'Hooli'
    }
}
