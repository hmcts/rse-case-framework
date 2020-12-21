package uk.gov.hmcts.ccf.controller.kase;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jooq.generated.enums.CaseState;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaseActions {
    private Long id;
    private CaseState state;
    private Set<String> actions;
}
