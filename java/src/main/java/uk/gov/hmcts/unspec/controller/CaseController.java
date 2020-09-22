package uk.gov.hmcts.unspec.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.ccf.CaseHandler;
import uk.gov.hmcts.ccf.api.ApiCase;
import uk.gov.hmcts.unspec.StatemachineConfig;
import uk.gov.hmcts.unspec.dto.Citizen;
import static org.jooq.impl.DSL.*;
import org.jooq.*;
import org.jooq.impl.*;

import java.io.IOException;
import java.util.*;

import static org.jooq.generated.tables.Citizen.CITIZEN;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200", exposedHeaders = {"Location"})
public class CaseController {

    @Autowired
    StatemachineConfig stateMachineSupplier;

    @Autowired
    CaseHandler caseHandler;

    @Autowired
    DefaultDSLContext create;

    @SneakyThrows
    @GetMapping(path = "/cases/{id}/citizens")
    @Transactional
    public List<Citizen> getParties(@PathVariable("id") String id,
                                    @RequestHeader("search-query") String base64JSONQuery,
                                    @RequestParam("page") Integer page) {
        byte[] bytes = Base64.getDecoder().decode(base64JSONQuery.getBytes());
        Map<String, String> query = new ObjectMapper().readValue(bytes, HashMap.class);
        Condition condition = DSL.trueCondition()
                .and(CITIZEN.CASE_ID.eq(Long.valueOf(id)));
        String forename = query.get("forename");
        if (!StringUtils.isEmpty(forename)) {
            condition = condition.and(lower(CITIZEN.FORENAME).like("%" + forename.toLowerCase() + "%"));
        }
        String surname = query.get("surname");
        if (!StringUtils.isEmpty(surname)) {
            condition = condition.and(lower(CITIZEN.SURNAME).like("%" + surname.toLowerCase() + "%"));
        }
        int offset = (page - 1) * 10;
        return create.select(CITIZEN.asterisk())
                .from(CITIZEN)
                .where(condition)
                .orderBy(CITIZEN.DATE_OF_BIRTH.desc())
                .limit(10)
                .offset(offset)
                .fetchInto(Citizen.class);
    }

}
