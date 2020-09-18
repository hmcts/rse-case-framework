package uk.gov.hmcts.unspec;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ccf.Case;
import uk.gov.hmcts.ccf.CaseHandler;
import uk.gov.hmcts.unspec.model.UnspecCase;
import uk.gov.hmcts.unspec.repository.CaseRepository;

import javax.persistence.EntityManager;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static org.jooq.generated.Tables.UNSPEC_CASES;

@Service
public class CaseHandlerImpl implements CaseHandler {

    @Autowired
    EntityManager em;

    @Autowired
    DefaultDSLContext create;

    @Autowired
    CaseRepository repository;

    @Override
    public JsonNode get(Long caseId) {
        UnspecCase c = repository.load(Long.valueOf(caseId));
        return new ObjectMapper().valueToTree(c);
    }

    @Override
    public Collection<Case> search(Map<String, String> params) {
        Object id = params.get("id");
        Condition condition = DSL.trueCondition();
        if (id != null && id.toString().length() > 0) {
            condition = condition.and(UNSPEC_CASES.CASE_ID.equal(Long.valueOf(id.toString())));
        }

        return create.select()
                .from(UNSPEC_CASES)
                .where(condition)
                .orderBy(UNSPEC_CASES.CASE_ID.asc())
                .fetch()
                .stream()
                // TODO
                .map(x -> new Case(x.get(UNSPEC_CASES.CASE_ID), null))
                .collect(Collectors.toUnmodifiableList());

    }
}
