package uk.gov.hmcts.unspec.model;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import uk.gov.hmcts.unspec.dto.Party;

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
    private List<Party> parties;

    private List<Claim> claims = Lists.newArrayList();
}
