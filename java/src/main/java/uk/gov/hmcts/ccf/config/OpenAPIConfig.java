package uk.gov.hmcts.ccf.config;

import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
public class OpenAPIConfig {

    /**
        Make all OpenAPI types have required properties by default.
     */
    @Bean
    public OpenApiCustomiser customerGlobalHeaderOpenApiCustomiser() {
        return openAPI -> openAPI.getComponents().getSchemas().values()
            .forEach(schema -> {
                if (schema.getProperties() != null) {
                    schema.setRequired(new ArrayList<>(schema.getProperties().keySet()));
                }
            });
    }
}
