package uk.gov.hmcts.unspec.event;


import lombok.*;
import uk.gov.hmcts.unspec.dto.LegalRepresentative;
import uk.gov.hmcts.unspec.dto.Party;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class CreateClaim {
    @NonNull
    private String claimantReference;
    @NonNull
    private String defendantReference;

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
