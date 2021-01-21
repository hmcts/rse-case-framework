package uk.gov.hmcts.ccd.v2.internal.resource;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import uk.gov.hmcts.ccd.domain.model.search.elasticsearch.SearchResultViewHeaderGroup;
import uk.gov.hmcts.ccd.domain.model.search.elasticsearch.SearchResultViewItem;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CaseSearchResultViewResource extends RepresentationModel {

    private List<SearchResultViewHeaderGroup> headers;
    private List<SearchResultViewItem> cases;
    private Long total;

}
