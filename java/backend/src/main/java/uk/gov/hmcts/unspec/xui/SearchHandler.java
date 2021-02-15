package uk.gov.hmcts.unspec.xui;

import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ccf.ColumnMapper;
import uk.gov.hmcts.ccf.ESQueryParser;
import uk.gov.hmcts.ccf.XUISearchHandler;

import java.util.List;

import static org.jooq.generated.Tables.CASES_WITH_STATES;
import static org.jooq.impl.DSL.asterisk;
import static org.jooq.impl.DSL.count;


@Component
public class SearchHandler extends XUISearchHandler<CaseSearchResult> {

    @Autowired
    private DefaultDSLContext jooq;

    @Override
    public void configureColumns(ColumnMapper<CaseSearchResult> mapper) {
        mapper.column(CaseSearchResult::getCaseId)
            .column(CaseSearchResult::getState)
            .column(CaseSearchResult::getParentCaseId);
    }

    public SearchResults search(ESQueryParser.ESQuery query) {
        List<CaseSearchResult> results = jooq
            .select(asterisk(), count().over().as("rowCount"))
            .from(CASES_WITH_STATES)
            .orderBy(CASES_WITH_STATES.CASE_ID.asc())
            .offset(query.getFrom())
            .limit(query.getPageSize())
            .fetchInto(CaseSearchResult.class);
        long rowCount = results.isEmpty() ? 0 : results.get(0).getRowCount();
        return new SearchResults(rowCount, results);
    }
}
