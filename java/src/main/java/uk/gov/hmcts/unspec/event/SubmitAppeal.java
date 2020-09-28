package uk.gov.hmcts.unspec.event;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SubmitAppeal {
    private String reason;
}
