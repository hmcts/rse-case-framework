package uk.gov.hmcts.ccf.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Claims {
    List<Long> claimant;
    List<Long> defendant;
}
