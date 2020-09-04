package uk.gov.hmcts.unspec;

import lombok.SneakyThrows;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import uk.gov.hmcts.ccf.StateMachine;
import uk.gov.hmcts.unspec.enums.Event;
import uk.gov.hmcts.unspec.enums.State;
import uk.gov.hmcts.unspec.event.AddNotes;
import uk.gov.hmcts.unspec.event.CloseCase;
import uk.gov.hmcts.unspec.event.CreateClaim;
import uk.gov.hmcts.unspec.event.SubmitAppeal;
import uk.gov.hmcts.unspec.model.UnspecCase;
import uk.gov.hmcts.unspec.repository.CaseRepository;

@Validated
@Configuration
public class StatemachineConfig {

    @Autowired
    DefaultDSLContext create;

    @Autowired
    CaseRepository repository;

    public StateMachine<State, Event> build() {
        StateMachine<State, Event> result = new StateMachine<>();
        result.initialState(State.Created, this::onCreate)
                .addUniversalEvent(Event.AddNotes, this::addNotes)
                .addTransition(State.Created, State.Closed, Event.CloseCase, this::closeCase)
                .addTransition(State.Closed, State.Stayed, Event.SubmitAppeal, this::closeCase);
        return result;
    }

    private void addNotes(Long id, AddNotes notes) {
        UnspecCase c = repository.load(id);
        c.getNotes().add(notes.getNotes());
        repository.save(c);
    }

    @SneakyThrows
    private void onCreate(Long id, CreateClaim request) {
        if (request.getClaimantReference().contains("@")) {
            throw new IllegalArgumentException("Invalid reference!");
        }

        UnspecCase data = new UnspecCase(id,
                request.getClaimant(), request.getDefendant());
        data.setCourtLocation(request.getApplicantPreferredCourt());

        repository.save(data);
    }

    private void reopenCase(String id, SubmitAppeal r) {

    }

    private void closeCase(Long id, CloseCase t) {

    }
}
