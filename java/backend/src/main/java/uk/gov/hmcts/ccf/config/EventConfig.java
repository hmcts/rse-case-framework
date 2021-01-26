package uk.gov.hmcts.ccf.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import org.jooq.JSONB;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseUpdateViewEvent;
import uk.gov.hmcts.ccf.EventBuilder;
import uk.gov.hmcts.unspec.dto.AddParty;

import java.util.Map;

import static org.jooq.generated.Tables.PARTIES;

@Component
public class EventConfig {

    @Autowired
    private DefaultDSLContext jooq;

    private Map<String, EventBuilder.CCFEvent> events = Maps.newHashMap();

    public EventConfig() {

        EventBuilder.CCFEvent e =
            new EventBuilder<>(AddParty.class, "addParty", "Add a party")
                .withHandler(this::addParty)
                .field(AddParty::getFirstName)
                .field(AddParty::getLastName)
                .build();
        events.put("addParty", e);
    }

    public CaseUpdateViewEvent getXUIEvent(String eventId) {
        return events.get(eventId).getViewEvent();
    }

    public EventBuilder.CCFEvent getEvent(String eventId)  {
        return events.get(eventId);
    }

    @SneakyThrows
    private void addParty(Long caseId, AddParty addParty) {
        jooq.insertInto(PARTIES, PARTIES.CASE_ID, PARTIES.DATA)
            .values(caseId, JSONB.valueOf(new ObjectMapper().writeValueAsString(addParty)))
            .execute();
    }
}
