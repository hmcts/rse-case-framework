package uk.gov.hmcts.ccf.types.fields;

import lombok.Getter;
import lombok.Setter;
import uk.gov.hmcts.ccf.definition.CaseListField;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Getter @Setter
public class HasDate {
    @CaseListField(label = "foo")
    private LocalDate date;
    @CaseListField(label = "bar")
    private LocalDateTime dateTime;
    @CaseListField(label = "baz")
    private Date third;
}
