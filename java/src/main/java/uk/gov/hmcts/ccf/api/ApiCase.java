package uk.gov.hmcts.ccf.api;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiCase {
    private Long id;
    private String state;
    private Set<String> actions;
    private JsonNode data;
}
