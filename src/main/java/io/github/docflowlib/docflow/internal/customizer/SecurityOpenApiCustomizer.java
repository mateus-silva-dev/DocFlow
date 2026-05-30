package io.github.docflowlib.docflow.internal.customizer;

import io.github.docflowlib.docflow.properties.DocflowProperties;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import io.swagger.v3.oas.models.Components;

public class SecurityOpenApiCustomizer implements OpenApiCustomizer {

    private final DocflowProperties properties;

    public SecurityOpenApiCustomizer(DocflowProperties properties) {
        this.properties = properties;
    }

    @Override
    public void customise(io.swagger.v3.oas.models.OpenAPI openApi) {
        if (!properties.getSecurity().isEnabled()) {
            return;
        }

        SecurityScheme securityScheme = new SecurityScheme()
                .name("bearerAuth")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat(properties.getSecurity().getBearerFormat())
                .description(properties.getSecurity().getSchemeName());

        if (openApi.getComponents() == null) {
            openApi.setComponents(new Components());
        }
        openApi.getComponents().addSecuritySchemes("bearerAuth", securityScheme);
    }
}
