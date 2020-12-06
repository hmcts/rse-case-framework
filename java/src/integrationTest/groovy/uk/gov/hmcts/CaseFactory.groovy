package uk.gov.hmcts

import com.fasterxml.jackson.databind.ObjectMapper
import org.jooq.impl.DefaultDSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import uk.gov.hmcts.ccf.api.ApiCase
import uk.gov.hmcts.ccf.api.ApiEventCreation
import uk.gov.hmcts.ccf.controller.CaseController
import uk.gov.hmcts.unspec.dto.Company
import uk.gov.hmcts.unspec.dto.Organisation
import uk.gov.hmcts.unspec.event.CreateClaim

@Component
class CaseFactory {

    @Autowired
    CaseController controller;

    @Autowired
    DefaultDSLContext jooq;

    ResponseEntity<ApiCase> CreateCase() {
        def event = CreateClaim.builder()
            .claimantReference("Foo")
            .defendantReference("Bar")
            .claimant(new Company("Hooli"))
            .defendant(new Organisation("Wiki"))
            .lowerValue(1)
            .higherValue(2)
            .build()
        def request = new ApiEventCreation("Create", new ObjectMapper().valueToTree(event))
        return controller.createCase(request, "A", "user")
    }
}
