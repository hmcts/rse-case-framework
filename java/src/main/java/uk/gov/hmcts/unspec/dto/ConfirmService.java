package uk.gov.hmcts.unspec.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ConfirmService {
    private Long claimId;
    private String name;
    private String role;
}
