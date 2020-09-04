package uk.gov.hmcts.unspec.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.jooq.JSONB;
import org.jooq.generated.tables.records.UnspecCasesRecord;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.unspec.model.UnspecCase;

import static org.jooq.generated.Tables.UNSPEC_CASES;
import static org.jooq.impl.DSL.select;

@Component
public class CaseRepository {

    @Autowired
    DefaultDSLContext create;

    @SneakyThrows
    public void save(UnspecCase unspecCase) {

        UnspecCasesRecord c = create.newRecord(UNSPEC_CASES);
        c.setCaseId(unspecCase.getId());
        c.setData(JSONB.valueOf(new ObjectMapper().writeValueAsString(unspecCase)));
        if (create.fetchExists(select()
                .from(UNSPEC_CASES)
                .where(UNSPEC_CASES.CASE_ID.eq(c.getCaseId())))) {
            c.update();
        } else {
            c.store();
        }
    }

    @SneakyThrows
    public UnspecCase load(Long l) {
        String data =create.select(UNSPEC_CASES.DATA)
                .from(UNSPEC_CASES)
                .where(UNSPEC_CASES.CASE_ID.eq(l))
                .fetchSingle(UNSPEC_CASES.DATA).data();

        return new ObjectMapper().readValue(data, UnspecCase.class);
    }
}
