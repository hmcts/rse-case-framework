package uk.gov.hmcts.ccf.demo.model;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import uk.gov.hmcts.ccf.demo.dto.Party;

import java.util.List;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class UnspecCase {
    @NonNull
    private Long id;
    private String name;
    private String courtLocation;
    private List<String> notes = Lists.newArrayList();
    @NonNull
    private Party claimant;
    @NonNull
    private Party defendant;
}
