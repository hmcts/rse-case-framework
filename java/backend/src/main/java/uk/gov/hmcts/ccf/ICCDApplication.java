package uk.gov.hmcts.ccf;

import com.google.common.collect.ImmutableSet;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseViewActionableEvent;
import uk.gov.hmcts.ccd.domain.model.aggregated.ProfileCaseState;
import uk.gov.hmcts.ccd.domain.model.std.CaseDataContent;

import java.util.List;
import java.util.Map;

public interface ICCDApplication<T extends ICase> {

    List<T> getCases(Map<String, String> searchCriteria);

    String saveCase(T c);

    T getCase(String id);

    ImmutableSet<String> getEvents();

    List<CaseViewActionableEvent> getTriggers(String caseId);

    void handleTrigger(String caseID, CaseDataContent caseDetails);

    ProfileCaseState getCaseState(String caseId);

    Map<String, Class> eventsMapping();
}
