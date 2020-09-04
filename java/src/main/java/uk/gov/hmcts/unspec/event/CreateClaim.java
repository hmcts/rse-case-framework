package uk.gov.hmcts.unspec.event;


import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.github.imifou.jsonschema.module.addon.annotation.JsonSchema;
import lombok.*;
import uk.gov.hmcts.unspec.dto.LegalRepresentative;
import uk.gov.hmcts.unspec.dto.Party;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@JsonClassDescription("Create Claim")
public class CreateClaim {
    @NonNull
    @JsonSchema(title = "Claimant's legal representative's reference")
    private String claimantReference;
    @NonNull
    @JsonSchema(title = "Defendant's legal representative's reference")
    private String defendantReference;

    @JsonSchema(title = "Court name")
    private String applicantPreferredCourt;

    private Party claimant;
    private Party defendant;

    private String defendantLegalRepEmail;
    private LegalRepresentative defendantLegalRep;

    private String claimType;
    private String claimSubType;
    private String claimDetails;
    private String claimParticulars;

    private int lowerValue;
    private int higherValue;

    private String feeAccountNumber;

    private String statementOfTruth;

}
