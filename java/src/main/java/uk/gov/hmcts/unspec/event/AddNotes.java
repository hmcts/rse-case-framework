package uk.gov.hmcts.unspec.event;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import lombok.Data;

@Data
public class AddNotes {
    private String notes;
}
