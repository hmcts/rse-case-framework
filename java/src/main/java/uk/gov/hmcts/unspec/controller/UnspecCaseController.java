package uk.gov.hmcts.unspec.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.ccf.CaseHandler;
import uk.gov.hmcts.unspec.CaseHandlerImpl;
import uk.gov.hmcts.unspec.dto.Citizen;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jooq.generated.tables.Citizen.CITIZEN;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.lower;

@RestController
@RequestMapping("/web")
public class UnspecCaseController {

    @Autowired
    CaseHandlerImpl stateMachineSupplier;

    @Autowired
    CaseHandler caseHandler;

    @Autowired
    DefaultDSLContext create;

    @SneakyThrows
    @GetMapping(path = "/cases/{id}/citizens")
    @Transactional
    public Map<String, Object> getCitizens(@PathVariable("id") String id,
                                           @RequestHeader("search-query") String base64JsonQuery,
                                           @RequestParam("page") Integer page) {
        byte[] bytes = Base64.getDecoder().decode(base64JsonQuery.getBytes());
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
        List<Citizen> result = create.select(count().over(), CITIZEN.asterisk())
                .from(CITIZEN)
                .where(condition)
                .orderBy(CITIZEN.DATE_OF_BIRTH.desc())
                .limit(11)
                .offset(offset)
                .fetchInto(Citizen.class);
        Map<String, Object> m = Maps.newHashMap();
        m.put("citizens", result.subList(0, Math.min(10, result.size())));
        m.put("hasMore", result.size() > 10);
        return m;
    }

    @SneakyThrows
    @GetMapping(path = "/cases/{id}/citizens/inactive")
    @Transactional
    public Map<String, Object> countInactive(@PathVariable("id") String caseId) {
        Map<String, Object> result = Maps.newHashMap();
        int count = create.select(count())
                .from(CITIZEN)
                .where(CITIZEN.STATUS.eq("Inactive"))
                .fetch().get(0).value1();
        result.put("inactive_count", count);
        return result;
    }
}
