package uk.gov.hmcts.ccf.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import uk.gov.hmcts.unspec.dto.Citizen;

import java.util.List;

@Data
@AllArgsConstructor
class CitizenResponse {
    boolean hasMore;
    List<Citizen> citizens;
}
