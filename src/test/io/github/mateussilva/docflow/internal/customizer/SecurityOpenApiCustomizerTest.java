package io.github.mateussilva.docflow.internal.customizer;

import io.github.docflowlib.docflow.internal.customizer.SecurityOpenApiCustomizer;
import io.github.docflowlib.docflow.properties.DocflowProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SecurityOpenApiCustomizerTest {

    @Test
    void shouldNotCreateSecuritySchemeWhenDisabled() {
        DocflowProperties properties = new DocflowProperties();
        OpenAPI openAPI = new OpenAPI();

        new SecurityOpenApiCustomizer(properties).customise(openAPI);

        assertNull(openAPI.getComponents());
    }

    @Test
    void shouldCreateBearerSecuritySchemeWhenEnabled() {
        DocflowProperties properties = new DocflowProperties();
        properties.getSecurity().setEnabled(true);
        properties.getSecurity().setBearerFormat("Opaque");
        properties.getSecurity().setSchemeName("Access token");
        OpenAPI openAPI = new OpenAPI();

        new SecurityOpenApiCustomizer(properties).customise(openAPI);

        SecurityScheme scheme = openAPI.getComponents().getSecuritySchemes().get("bearerAuth");
        assertEquals(SecurityScheme.Type.HTTP, scheme.getType());
        assertEquals("bearer", scheme.getScheme());
        assertEquals("Opaque", scheme.getBearerFormat());
        assertEquals("Access token", scheme.getDescription());
    }
}
