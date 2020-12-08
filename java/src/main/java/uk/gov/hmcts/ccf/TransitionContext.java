package uk.gov.hmcts.ccf;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransitionContext {
    private String userId;
    private Long entityId;
}
