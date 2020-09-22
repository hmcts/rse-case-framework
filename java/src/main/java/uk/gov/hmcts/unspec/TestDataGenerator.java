package uk.gov.hmcts.unspec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import lombok.SneakyThrows;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ccf.api.ApiEventCreation;
import uk.gov.hmcts.ccf.controller.WebController;
import uk.gov.hmcts.unspec.dto.Company;
import uk.gov.hmcts.unspec.dto.Organisation;
import uk.gov.hmcts.unspec.enums.Event;
import uk.gov.hmcts.unspec.event.CreateClaim;

import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.jooq.generated.Tables.EVENTS;
import static org.jooq.impl.DSL.count;

@Component
public class TestDataGenerator implements InitializingBean {

    @Autowired
    private WebController controller;

    @Value("${generate-data:false}")
    public String generate;

    @Autowired
    DefaultDSLContext create;

    @SneakyThrows
    @Override
    public void afterPropertiesSet() {
        if (!"true".equals(generate)) {
            return;
        }
        int count = create.select(count()).from(EVENTS).fetchSingle().value1();
        if (count > 0) {
            return;
        }

        CreateClaim o = CreateClaim.builder()
        .claimantReference("666")
                .defendantReference("999")
                .claimant(new Company("Acme Ltd"))
                .defendant(new Organisation("Megacorp Inc"))
                .build();
        ApiEventCreation e = new ApiEventCreation(Event.CreateClaim, new ObjectMapper().valueToTree(o));
        controller.createCase(e);

        o = CreateClaim.builder()
                .claimantReference("1111")
                .defendantReference("33333")
                .claimant(new Company("Hooli"))
                .defendant(new Organisation("Wiki"))
                .build();
        e = new ApiEventCreation(Event.CreateClaim, new ObjectMapper().valueToTree(o));
        controller.createCase(e);

        URL url = Resources.getResource("seed_data/seed.sql");
        String sql = Resources.toString(url, StandardCharsets.UTF_8);
        create.execute(sql);
    }

}
