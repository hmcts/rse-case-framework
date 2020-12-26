package uk.gov.hmcts.ccf.controller.citizen;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.hmcts.unspec.dto.Citizen;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.jooq.generated.tables.Citizen.CITIZEN;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.lower;

@RestController
@RequestMapping("/web")
public class CitizenController {
    @Autowired
    DefaultDSLContext jooq;

    @SneakyThrows
    @GetMapping(path = "/cases/{id}/citizens")
    @Transactional
    public CitizenResponse getCitizens(@PathVariable("id") Long id,
                                           @RequestHeader("search-query") String base64JsonQuery,
                                           @RequestParam("page") Integer page) {
        byte[] bytes = Base64.getDecoder().decode(base64JsonQuery.getBytes());
        Map<String, String> query = new ObjectMapper().readValue(bytes, HashMap.class);
        Condition condition = DSL.trueCondition()
            .and(CITIZEN.CASE_ID.eq(id));
        String forename = query.get("forename");
        if (!StringUtils.isEmpty(forename)) {
            condition = condition.and(lower(CITIZEN.FORENAME).like("%" + forename.toLowerCase() + "%"));
        }
        String surname = query.get("surname");
        if (!StringUtils.isEmpty(surname)) {
            condition = condition.and(lower(CITIZEN.SURNAME).like("%" + surname.toLowerCase() + "%"));
        }
        int offset = (page - 1) * 10;
        List<Citizen> result = jooq.select(count().over(), CITIZEN.asterisk())
            .from(CITIZEN)
            .where(condition)
            .orderBy(CITIZEN.DATE_OF_BIRTH.desc())
            .limit(11)
            .offset(offset)
            .fetchInto(Citizen.class);
        return new CitizenResponse(
            result.size() > 10,
            result.subList(0, Math.min(10, result.size()))
        );
    }

    @SneakyThrows
    @GetMapping(path = "/cases/{id}/citizens/inactive")
    @Transactional
    public long countInactive(@PathVariable("id") String caseId) {
        Map<String, Object> result = Maps.newHashMap();
        int count = jooq.select(count())
            .from(CITIZEN)
            .where(CITIZEN.STATUS.eq("Inactive"))
            .fetch().get(0).value1();
        return count;
    }

    @PostMapping(path = "/cases/{caseId}/files")
    @Transactional
    public ResponseEntity<String> fileUpload(@PathVariable("caseId") Long caseId,
                                             @RequestParam("file") MultipartFile file,
                                             @Parameter(hidden = true) @AuthenticationPrincipal  OidcUser user) {
        bulkImport(caseId, file);
        return ResponseEntity.created(URI.create("/cases/" + caseId))
            .body("");
    }

    @SneakyThrows
    private void bulkImport(Long caseId, MultipartFile f) {
        try (InputStream is = f.getInputStream()) {
            CSVParser records = CSVFormat.DEFAULT.parse(new InputStreamReader(is));
            // Add the caseId column
            List<Object[]> rows = records.getRecords().stream().map(x -> {
                return new Object[]{caseId, x.get(0), x.get(1), x.get(2), x.get(3), x.get(4)};
            }).collect(Collectors.toList());
            jooq.loadInto(CITIZEN)
                .loadArrays(rows)
                .fields(CITIZEN.CASE_ID, CITIZEN.TITLE, CITIZEN.FORENAME, CITIZEN.SURNAME, CITIZEN.DATE_OF_BIRTH,
                    CITIZEN.STATUS)
                .execute();
        }
    }

    private void purgeInactive(Long caseId, Object o) {
        jooq.delete(CITIZEN)
            .where(CITIZEN.STATUS.eq("Inactive"))
            .execute();
    }

    @Data
    @AllArgsConstructor
    static class CitizenResponse {
        boolean hasMore;
        List<Citizen> citizens;
    }
}
