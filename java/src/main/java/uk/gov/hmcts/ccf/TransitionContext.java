package uk.gov.hmcts.ccf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TransitionContext {
    private String userId;
    private Long entityId;
}
