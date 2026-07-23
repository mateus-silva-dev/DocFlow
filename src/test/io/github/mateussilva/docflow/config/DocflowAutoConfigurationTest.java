package io.github.mateussilva.docflow.config;

import io.github.docflowlib.docflow.internal.config.DocflowAutoConfiguration;
import io.github.docflowlib.docflow.internal.customizer.DocFlowSchemaNameConverter;
import io.github.docflowlib.docflow.internal.customizer.OperationResponseCustomizer;
import io.github.docflowlib.docflow.internal.customizer.OperationTextCustomizer;
import io.github.docflowlib.docflow.internal.response.ErrorSchemaFactory;
import io.github.docflowlib.docflow.internal.response.ResponseCodeResolver;
import io.github.docflowlib.docflow.internal.response.ResponseFactory;
import io.github.docflowlib.docflow.properties.DocflowProperties;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;

import java.util.Optional;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DocflowAutoConfigurationTest {

    private final DocflowAutoConfiguration configuration = new DocflowAutoConfiguration();

    @Test
    void shouldCreateMessageSource() {
        MessageSource messageSource = configuration.docflowMessageSource();
        assertNotNull(messageSource);
    }

    @Test
    void shouldCreateResponseCodeResolver() {
        ResponseCodeResolver resolver = configuration.responseCodeResolver();
        assertNotNull(resolver);
    }

    @Test
    void shouldCreateErrorSchemaFactory() {
        DocflowProperties properties = new DocflowProperties();
        ErrorSchemaFactory factory = configuration.errorSchemaFactory(properties);
        assertNotNull(factory);
    }

    @Test
    void shouldCreateResponseFactory() {
        MessageSource messageSource = mock(MessageSource.class);
        ErrorSchemaFactory errorSchemaFactory = mock(ErrorSchemaFactory.class);
        ResponseFactory factory = configuration.responseFactory(messageSource, errorSchemaFactory);
        assertNotNull(factory);
    }

    @Test
    void shouldCreateOperationTextCustomizer() {
        MessageSource messageSource = mock(MessageSource.class);
        OperationTextCustomizer customizer = configuration.operationTextCustomizer(messageSource);
        assertNotNull(customizer);
    }

    @Test
    void shouldCreateOperationResponseCustomizer() {
        ResponseCodeResolver responseCodeResolver = mock(ResponseCodeResolver.class);
        ResponseFactory responseFactory = mock(ResponseFactory.class);
        OperationResponseCustomizer customizer = configuration.operationResponseCustomizer(responseCodeResolver, responseFactory);
        assertNotNull(customizer);
    }

    @Test
    void shouldCreateSecurityOpenApiCustomizer() {
        DocflowProperties properties = new DocflowProperties();
        OpenApiCustomizer customizer = configuration.securityOpenApiCustomizer(properties);

        assertNotNull(customizer);
    }

    @Test
    void shouldCreateSecurityOperationCustomizer() {
        DocflowProperties properties = new DocflowProperties();
        OperationCustomizer customizer = configuration.securityOperationCustomizer(properties);

        assertNotNull(customizer);
    }

    @Test
    void shouldCreateSchemaNameConverter() {
        DocFlowSchemaNameConverter converter = configuration.docFlowSchemaNameConverter();

        assertNotNull(converter);
    }

    @Test
    void shouldCreateOpenApiUsingExplicitProperties() {
        DocflowProperties properties = new DocflowProperties();
        properties.setTitle("Explicit API");
        properties.setDescription("Explicit description");
        properties.setVersion("9.9.9");

        Environment environment = mock(Environment.class);

        OpenAPI openAPI = configuration.docflowOpenApi(properties, environment, Optional.empty());

        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        assertEquals("Explicit API", openAPI.getInfo().getTitle());
        assertEquals("Explicit description", openAPI.getInfo().getDescription());
        assertEquals("9.9.9", openAPI.getInfo().getVersion());
    }

    @Test
    void shouldCreateOpenApiUsingFormattedApplicationNameWhenTitleIsEmpty() {
        DocflowProperties properties = new DocflowProperties();
        properties.setDescription("Generated description");

        Environment environment = mock(Environment.class);
        when(environment.getProperty("spring.application.name")).thenReturn("docflow-test-api");

        OpenAPI openAPI = configuration.docflowOpenApi(properties, environment, Optional.empty());

        assertEquals("Docflow Test Api", openAPI.getInfo().getTitle());
        assertEquals("Generated description", openAPI.getInfo().getDescription());
        assertEquals("1.0.0", openAPI.getInfo().getVersion());
    }

    @Test
    void shouldUseDefaultTitleWhenTitleAndApplicationNameAreEmpty() {
        DocflowProperties properties = new DocflowProperties();

        Environment environment = mock(Environment.class);
        when(environment.getProperty("spring.application.name")).thenReturn(null);

        OpenAPI openAPI = configuration.docflowOpenApi(properties, environment, Optional.empty());

        assertEquals("API Documentation", openAPI.getInfo().getTitle());
    }

    @Test
    void shouldUseBuildPropertiesVersionWhenConfiguredVersionIsDefault() {
        DocflowProperties properties = new DocflowProperties();

        Environment environment = mock(Environment.class);
        when(environment.getProperty("spring.application.name")).thenReturn(null);

        Properties buildPropertiesValues = new Properties();
        buildPropertiesValues.setProperty("version", "3.2.1");

        BuildProperties buildProperties = new BuildProperties(buildPropertiesValues);

        OpenAPI openAPI = configuration.docflowOpenApi(properties, environment, Optional.of(buildProperties));

        assertEquals("3.2.1", openAPI.getInfo().getVersion());
    }
}
