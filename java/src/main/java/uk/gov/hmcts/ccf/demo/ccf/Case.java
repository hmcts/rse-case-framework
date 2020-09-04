package uk.gov.hmcts.ccf.demo.ccf;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Case {
    private Long id;
    private JsonNode data;
}
