package uk.gov.hmcts.ccd.v2.internal.controller;

import org.jooq.generated.tables.pojos.CaseHistory;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseView;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewActionableEvent;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewEvent;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewJurisdiction;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewTab;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewType;
import uk.gov.hmcts.ccd.domain.model.aggregated.ProfileCaseState;
import uk.gov.hmcts.ccd.v2.V2;
import uk.gov.hmcts.ccd.v2.internal.resource.CaseHistoryViewResource;
import uk.gov.hmcts.ccd.v2.internal.resource.CaseViewResource;
import uk.gov.hmcts.ccf.CaseViewBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static org.jooq.generated.Tables.CASE_HISTORY;

@RestController
@RequestMapping(path = "/data/internal/cases")
public class UICaseController {
    private static final String ERROR_CASE_ID_INVALID = "Case ID is not valid";

    @Autowired
    private DefaultDSLContext jooq;

    @GetMapping(
        path = "/{caseId}",
        headers = {
            V2.EXPERIMENTAL_HEADER
        },
        produces = {
            V2.MediaType.UI_CASE_VIEW
        }
    )
    public ResponseEntity<CaseViewResource> getCaseView(@PathVariable("caseId") String caseId) {

        List<CaseViewEvent> history = getCaseViewHistory(caseId);

        List<CaseViewTab> tabs = new CaseViewBuilder()
            .newTab("History", "History")
            .field("History", history, null, "CaseHistoryViewer")
            .build()
            .newTab("Petition", "Petition")
            .textField("Place of marriage", "Cathedral", "")
            .build()
            .build();

        CaseView view = buildCaseView(caseId);
        view.setTabs(tabs);

        return ResponseEntity.ok(new CaseViewResource(view));
    }

    private List<CaseViewEvent> getCaseViewHistory(String caseId) {
        List<CaseHistory> hist = jooq.select()
            .from(CASE_HISTORY)
            .where(CASE_HISTORY.CASE_ID.eq(Long.valueOf(caseId)))
            .orderBy(CASE_HISTORY.TIMESTAMP.desc())
            .fetchInto(CaseHistory.class);
        return hist.stream().map(x -> CaseViewEvent.builder()
            .id(Long.valueOf(x.hashCode()))
            .timestamp(x.getTimestamp())
            .summary(x.getId())
            .eventId(x.getId())
            .eventName(x.getId())
            .userId(x.getUserId())
            .userFirstName(x.getUserForename())
            .userLastName(x.getUserSurname())
            .stateId("Open")
            .stateName("Open")
            .build()).collect(toList());
    }

    private CaseView buildCaseView(String caseId) {
        CaseView caseView = new CaseView();
        caseView.setCaseId(caseId);
        caseView.setChannels(getChannels());

        caseView.setActionableEvents(getActionableEvents());

        caseView.setState(getState());
        CaseViewJurisdiction jurisdiction = new CaseViewJurisdiction();
        jurisdiction.setId("NFD");
        jurisdiction.setName("No fault divorce");
        jurisdiction.setDescription("No fault divorce");
        CaseViewType caseType = new CaseViewType();
        caseType.setJurisdiction(jurisdiction);
        caseType.setDescription("NFD");
        caseType.setId(caseId);
        caseView.setCaseType(caseType);
        caseView.setEvents(getCaseViewEvents());
        return caseView;
    }

    private List<CaseViewActionableEvent> getActionableEvents() {
        return List.of(
            CaseViewActionableEvent.builder()
                .id("generalReferral")
                .name("generalReferral")
                .description("generalReferral")
                .order(1)
                .build()
        );
    }

    private ProfileCaseState getState() {
        return new ProfileCaseState("Open", "Open", "Open", "Open");
    }

    private CaseViewEvent createCaseViewEvent(String s) {
        CaseViewEvent event = new CaseViewEvent();
        event.setId(new Random().nextLong());
        event.setComment("Hardcoded TODO");
        event.setEventName(s);
        event.setStateId("Hardcoded TODO");
        event.setStateName("Hardcoded TODO");
        event.setSummary(s);
        event.setTimestamp(LocalDateTime.now());
        event.setUserFirstName("First");
        event.setUserLastName("Last");
        event.setEventId("Hardcoded TODO");
        event.setUserId(String.valueOf(new Random().nextLong()));
        return event;
    }

    private CaseViewEvent[] getCaseViewEvents() {
        Set<String> events = Set.of("AddNotes");
        List<CaseViewEvent> collect = events
            .stream()
            .map(this::createCaseViewEvent)
            .collect(toList());

        CaseViewEvent[] caseViewEvents = new CaseViewEvent[collect.size()];
        return collect.toArray(caseViewEvents);
    }

    private String[] getChannels() {
        String[] strings = new String[1];
        strings[0] = "channel1";
        return strings;
    }

    @GetMapping(
        path = "/{caseId}/events/{eventId}",
        headers = {
            V2.EXPERIMENTAL_HEADER
        },
        produces = {
            V2.MediaType.UI_EVENT_VIEW
        }
    )
    public ResponseEntity<CaseHistoryViewResource> getCaseHistoryView(@PathVariable("caseId") String caseId,
                                                                             @PathVariable("eventId") String eventId) {
        throw new RuntimeException();
    }
}
