package uk.gov.hmcts.ccf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseView;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewActionableEvent;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewEvent;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewJurisdiction;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewType;
import uk.gov.hmcts.ccd.domain.model.definition.CaseEventDefinition;
import uk.gov.hmcts.ccd.domain.model.definition.CaseFieldDefinition;
import uk.gov.hmcts.ccd.domain.model.definition.CaseStateDefinition;
import uk.gov.hmcts.ccd.domain.model.definition.CaseTypeDefinition;
import uk.gov.hmcts.ccd.domain.model.search.SearchInput;
import uk.gov.hmcts.ccd.domain.model.search.WorkbasketInput;
import uk.gov.hmcts.ccd.domain.model.std.CaseDataContent;
import uk.gov.hmcts.ccf.definition.ICaseView;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@SuppressWarnings("unchecked")
public class CoreCaseService {
    private final ICcdApplication application;
    private final CCDAppConfig config;
    private final Class caseClass;

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();
    private List<ICaseView> views;

    public CoreCaseService(CCDAppConfig config, ICcdApplication application, List<ICaseView> views) {
        this.config = config;
        this.application = application;
        this.views = views;
        this.caseClass = ReflectionUtils.getCaseType(application.getClass());
    }

    public CaseTypeDefinition getCaseType() {
        CaseTypeDefinition result = new CaseTypeDefinition();
        result.setId(config.getCaseTypeId());
        result.setName(config.getCaseTypeId());
        result.setDescription(config.getCaseTypeId());
        result.setCaseFieldDefinitions(ReflectionUtils.getCaseListFields(caseClass));

        List<CaseStateDefinition> states = Lists.newArrayList();
        ReflectionUtils.extractStates(caseClass).stream().forEach(x -> states.add(createState(x.toString())));

        result.setStates(states);
        result.setEvents(getCaseEvents());
        return result;
    }

    private List<CaseEventDefinition> getCaseEvents() {
        List<CaseEventDefinition> events = Lists.newArrayList();
        application.getEvents().forEach(x -> events.add(createEvent(x.toString())));
        return events;
    }

    private CaseViewEvent[] getCaseViewEvents() {
        Set<String> events = application.getEvents();
        List<CaseViewEvent> collect = events
                .stream()
                .map(this::createCaseViewEvent)
                .collect(toList());

        CaseViewEvent[] caseViewEvents = new CaseViewEvent[collect.size()];
        return collect.toArray(caseViewEvents);
    }


    public CaseView getCaseView(String jurisdictionId, String caseTypeId, String caseId) {
        CaseView caseView = new CaseView();
        caseView.setCaseId(caseId);
        caseView.setTabs(ReflectionUtils.generateCaseViewTabs(application.getCase(caseId), views));
        caseView.setChannels(getChannels());
        // TODO
        //        caseView.setTriggers(getTriggers(caseId));

        caseView.setState(application.getCaseState(caseId));
        CaseViewJurisdiction jurisdiction = new CaseViewJurisdiction();
        jurisdiction.setId(jurisdictionId);
        jurisdiction.setName(jurisdictionId);
        jurisdiction.setDescription(jurisdictionId);
        CaseViewType caseType = new CaseViewType();
        caseType.setJurisdiction(jurisdiction);
        caseType.setDescription(jurisdictionId);
        caseType.setId(caseTypeId);
        caseView.setCaseType(caseType);
        caseView.setEvents(getCaseViewEvents());

        return caseView;
    }

    private CaseViewActionableEvent[] getTriggers(String caseId) {
        return (CaseViewActionableEvent[]) application.getTriggers(caseId).toArray();
    }

    private String[] getChannels() {
        String[] strings = new String[1];
        strings[0] = "channel1";
        return strings;
    }

    private CaseEventDefinition createEvent(String s) {
        CaseEventDefinition event = new CaseEventDefinition();
        event.setId(s);
        event.setName(s);
        return event;
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

    private CaseStateDefinition createState(String name) {
        CaseStateDefinition result = new CaseStateDefinition();
        result.setId(name);
        result.setName(name);
        return result;
    }

    public WorkbasketInput[] getWorkBasketInputs() {
        return ReflectionUtils.generateWorkbasketInputs(caseClass).toArray(new WorkbasketInput[0]);
    }

    public SearchInput[] searchInputs() {
        return ReflectionUtils.generateSearchInputs(caseClass).toArray(new SearchInput[0]);
    }

    public String onCaseCreated(JsonNode node) throws JsonProcessingException {
        ICase c = (ICase) objectMapper.treeToValue(node, caseClass);
        return application.saveCase(c);
    }

    public CaseViewActionableEvent getCaseEventTrigger(String caseId, String eventTriggerId) {

        Class eventType = (Class) application.eventsMapping().get(eventTriggerId);

        List<CaseFieldDefinition> fields = ReflectionUtils.getCaseViewFieldForEvent(eventType);

        CaseViewActionableEvent caseEventTrigger = new CaseViewActionableEvent();
        //        caseEventTrigger.setCaseFields(fields);
        //        caseEventTrigger.setCaseId(caseId);
        //        caseEventTrigger.setId(eventTriggerId);
        //        caseEventTrigger.setDescription("blah");
        //        caseEventTrigger.setName(eventTriggerId);
        //        caseEventTrigger.setEventToken("hi");
        //        caseEventTrigger.setWizardPages(
        //            singletonList(
        //                new WizardPage(
        //                    UUID.randomUUID().toString(),
        //                    null,
        //                    null,
        //                    fields.stream()
        //                        .map(f -> new WizardPageField(
        //                            f.getId(),
        //                            null,
        //                            null
        //                        ))
        //                        .collect(toList()),
        //                    null
        //                )
        //            ));

        return caseEventTrigger;
    }

    public void handleTrigger(String caseID, CaseDataContent caseDetails) {
        application.handleTrigger(caseID, caseDetails);
    }
}
