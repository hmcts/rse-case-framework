package uk.gov.hmcts.ccd.domain.model.search.elasticsearch;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class SearchResultViewHeaderGroup {

    @NonNull
    private HeaderGroupMetadata metadata;
    @NonNull
    private List<SearchResultViewHeader> fields;
    @NonNull
    private List<String> cases;
}
