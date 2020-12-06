package uk.gov.hmcts.ccf.controller;

import lombok.SneakyThrows;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static org.jooq.generated.tables.Citizen.CITIZEN;

@RestController
@RequestMapping("/web")
public class CitizenController {
    @Autowired
    DefaultDSLContext jooq;

    @PostMapping(path = "/cases/{caseId}/files")
    @Transactional
    public ResponseEntity<String> fileUpload(@PathVariable("caseId") Long caseId,
                                             @RequestParam("file") MultipartFile file,
                                             @AuthenticationPrincipal  OidcUser user) {
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
}
