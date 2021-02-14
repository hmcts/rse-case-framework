package uk.gov.hmcts.unspec.xui;

import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ccf.ColumnMapper;
import uk.gov.hmcts.ccf.ESQueryParser;
import uk.gov.hmcts.ccf.XUISearchHandler;

import java.util.Collection;

import static org.jooq.generated.Tables.CASES_WITH_STATES;
import static org.jooq.generated.Tables.PARTIES;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.table;


@Component
public class SearchHandler extends XUISearchHandler<CaseSearchResult> {

    @Autowired
    private DefaultDSLContext jooq;

    @Override
    public void configureColumns(ColumnMapper<CaseSearchResult> mapper) {
        mapper.column(CaseSearchResult::getCaseId)
            .column(CaseSearchResult::getState)
            .column(CaseSearchResult::getParentCaseId)
            .column(CaseSearchResult::getPartyCount);
    }

    public Collection<CaseSearchResult> search(ESQueryParser.ESQuery query) {
        return
            jooq.with("party_counts").as(
                select(PARTIES.CASE_ID, count().as("party_count"))
                    .from(PARTIES)
                    .groupBy(PARTIES.CASE_ID)
            )
                .select()
                .from(CASES_WITH_STATES)
                .join(table("party_counts")).using(CASES_WITH_STATES.CASE_ID)
                .orderBy(CASES_WITH_STATES.CASE_ID.asc())
                .offset(query.getFrom())
                .limit(query.getPageSize())
                .fetchInto(CaseSearchResult.class);
    }
}
