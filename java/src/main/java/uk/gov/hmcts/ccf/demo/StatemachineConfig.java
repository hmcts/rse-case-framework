package uk.gov.hmcts.ccf.demo;

import lombok.SneakyThrows;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import uk.gov.hmcts.ccf.demo.enums.Event;
import uk.gov.hmcts.ccf.demo.enums.State;
import uk.gov.hmcts.ccf.demo.event.AddNotes;
import uk.gov.hmcts.ccf.demo.event.CloseCase;
import uk.gov.hmcts.ccf.demo.event.CreateClaim;
import uk.gov.hmcts.ccf.demo.event.SubmitAppeal;
import uk.gov.hmcts.ccf.demo.model.UnspecCase;
import uk.gov.hmcts.ccf.demo.repository.CaseRepository;

import static uk.gov.hmcts.ccf.demo.enums.Event.AddNotes;
import static uk.gov.hmcts.ccf.demo.enums.Event.CloseCase;
import static uk.gov.hmcts.ccf.demo.enums.State.*;

@Validated
@Configuration
public class StatemachineConfig {

    @Autowired
    DefaultDSLContext create;

    @Autowired
    CaseRepository repository;

    public StateMachine<State, Event> build() {
        StateMachine<State, Event> result = new StateMachine<>();
        result.initialState(Created, this::onCreate)
                .addUniversalEvent(AddNotes, this::addNotes)
                .addTransition(Created, Closed, CloseCase, this::closeCase)
                .addTransition(Closed, Stayed, Event.SubmitAppeal, this::closeCase);
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
