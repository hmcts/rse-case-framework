package uk.gov.hmcts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uk.gov.hmcts.ccf.OidcWaiter;

@SpringBootApplication
@SuppressWarnings({"checkstyle:hideutilityclassconstructor"})
public class DemoApplication {

    public static void main(String[] args) {
        OidcWaiter.waitForOidcServer();
        SpringApplication.run(DemoApplication.class, args);
    }

}
