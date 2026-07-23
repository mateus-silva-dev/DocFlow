package io.github.mateussilva.docflow.properties;

import io.github.docflowlib.docflow.properties.DocflowProperties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DocFlowPropertiesTest {

    @Test
    void shouldHaveDefaultVersion() {
        DocflowProperties properties = new DocflowProperties();

        assertEquals("1.0.0", properties.getVersion());
    }

    @Test
    void shouldHaveDefaultSecurityProperties() {
        DocflowProperties properties = new DocflowProperties();

        assertEquals(false, properties.getSecurity().isEnabled());
        assertEquals("Bearer Authentication", properties.getSecurity().getSchemeName());
        assertEquals("JWT", properties.getSecurity().getBearerFormat());
    }

    @Test
    void shouldSetAndGetProperties() {
        DocflowProperties properties = new DocflowProperties();

        properties.setTitle("My API");
        properties.setDescription("API description");
        properties.setVersion("2.0.0");
        properties.setDefaultErrorSchema("com.example.ErrorResponse");

        assertEquals("My API", properties.getTitle());
        assertEquals("API description", properties.getDescription());
        assertEquals("2.0.0", properties.getVersion());
        assertEquals("com.example.ErrorResponse", properties.getDefaultErrorSchema());
    }

    @Test
    void shouldSetAndGetSecurityProperties() {
        DocflowProperties.Security security = new DocflowProperties.Security();
        security.setEnabled(true);
        security.setSchemeName("Token auth");
        security.setBearerFormat("Opaque");

        DocflowProperties properties = new DocflowProperties();
        properties.setSecurity(security);

        assertEquals(true, properties.getSecurity().isEnabled());
        assertEquals("Token auth", properties.getSecurity().getSchemeName());
        assertEquals("Opaque", properties.getSecurity().getBearerFormat());
    }

}
