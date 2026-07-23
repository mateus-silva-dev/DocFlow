package io.github.mateussilva.docflow.internal.response;

import io.github.docflowlib.docflow.internal.response.ErrorSchemaFactory;
import io.github.docflowlib.docflow.properties.DocflowProperties;
import io.swagger.v3.oas.models.media.Content;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ErrorSchemaFactoryTest {

    @Test
    void shouldReturnNullWhenDefaultErrorSchemaIsBlank() {
        DocflowProperties properties = new DocflowProperties();
        properties.setDefaultErrorSchema(" ");

        assertNull(new ErrorSchemaFactory(properties).create());
    }

    @Test
    void shouldCreateJsonContentForConfiguredErrorSchema() {
        DocflowProperties properties = new DocflowProperties();
        properties.setDefaultErrorSchema(StandardError.class.getName());

        Content content = new ErrorSchemaFactory(properties).create();

        assertNotNull(content);
        assertNotNull(content.get(MediaType.APPLICATION_JSON_VALUE));
        assertNotNull(content.get(MediaType.APPLICATION_JSON_VALUE).getSchema());
    }

    @Test
    void shouldThrowWhenConfiguredErrorSchemaDoesNotExist() {
        DocflowProperties properties = new DocflowProperties();
        properties.setDefaultErrorSchema("com.example.DoesNotExist");

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> new ErrorSchemaFactory(properties).create()
        );

        assertTrue(exception.getMessage().contains("com.example.DoesNotExist"));
    }

    static class StandardError {
        public String code;
        public String message;
    }
}
