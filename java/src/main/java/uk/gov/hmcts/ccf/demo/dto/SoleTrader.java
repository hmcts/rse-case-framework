package uk.gov.hmcts.ccf.demo.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SoleTrader extends Individual {
    private String tradingName;
}
