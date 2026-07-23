package io.github.docflowlib.docflow.internal.config;

import io.github.docflowlib.docflow.internal.customizer.*;
import io.github.docflowlib.docflow.internal.customizer.SecurityOpenApiCustomizer;
import io.github.docflowlib.docflow.internal.response.ErrorSchemaFactory;
import io.github.docflowlib.docflow.internal.response.ResponseCodeResolver;
import io.github.docflowlib.docflow.internal.response.ResponseFactory;
import io.github.docflowlib.docflow.properties.DocflowProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * Auto-configuration engine for the DocFlow framework integration.
 *
 * <p>This class bootstraps all the necessary customizers, internationalization bundles,
 * and custom OpenAPI configuration models required to drive the automated
 * documentation generation process.</p>
 *
 * @author Mateus Silva
 * @since 1.0.0
 */
@AutoConfiguration
@EnableConfigurationProperties(DocflowProperties.class)
public class DocflowAutoConfiguration {

    /**
     * Registers the internal MessageSource bundle responsible for driving the
     * multi-language internationalization (i18n) support within DocFlow.
     *
     * @return the configured internal message source bundle
     *
     * @author Mateus Silva
     * @since 1.0.0
     */
    @Bean(name = "docflowMessageSource")
    public MessageSource docflowMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("docflow-messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    /**
     * Registers the structural operation customizer built to automate swagger summaries
     * parsing from endpoints methods reflections.
     *
     * @param messageSource the isolated internal message source for localization contracts
     * @return the operational customizer instance
     *
     * @author Mateus Silva
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(name = "operationTextCustomizer")
    public OperationTextCustomizer operationTextCustomizer(@Qualifier("docflowMessageSource") MessageSource messageSource) {
        return new OperationTextCustomizer(messageSource);
    }

    /**
     * Provides the default {@link ResponseCodeResolver} bean.
     * <p>
     * This component is responsible for analyzing controller methods and determining
     * the correct HTTP success status code (e.g., 200 OK, 201 Created, 204 No Content).
     * <p>
     * It is only created if no other bean named "responseCodeResolver" is present in the context,
     * allowing applications to provide their own custom implementation if needed.
     *
     * @return a new instance of {@link ResponseCodeResolver}
     *
     * @author Mateus Silva
     * @since 2.0.0
     */
    @Bean
    @ConditionalOnMissingBean(name = "responseCodeResolver")
    public ResponseCodeResolver responseCodeResolver() {
        return new ResponseCodeResolver();
    }

    /**
     * Provides the default {@link ErrorSchemaFactory} bean.
     * <p>
     * This factory generates the OpenAPI schemas for error responses based on the
     * configuration provided in the library's properties.
     *
     * @param docflowProperties the configuration properties for Docflow
     * @return a new instance of {@link ErrorSchemaFactory}
     *
     * @author Mateus Silva
     * @since 2.0.0
     */
    @Bean
    @ConditionalOnMissingBean(name = "errorSchemaFactory")
    public ErrorSchemaFactory errorSchemaFactory(DocflowProperties docflowProperties) {
        return new ErrorSchemaFactory(docflowProperties);
    }

    /**
     * Provides the default {@link ResponseFactory} bean.
     * <p>
     * This factory is responsible for building the actual Swagger {@link io.swagger.v3.oas.models.responses.ApiResponse}
     * objects, integrating the standardized error schemas and localized messages.
     *
     * @param messageSource      the message source dedicated to Docflow for localized descriptions
     * @param errorSchemaFactory the factory used to attach standard error schemas to the responses
     * @return a new instance of {@link ResponseFactory}
     *
     * @author Mateus Silva
     * @since 2.0.0
     */
    @Bean
    @ConditionalOnMissingBean(name = "responseFactory")
    public ResponseFactory responseFactory(@Qualifier("docflowMessageSource") MessageSource messageSource, ErrorSchemaFactory errorSchemaFactory) {
        return new ResponseFactory(messageSource, errorSchemaFactory);
    }

    /**
     * Provides the default {@link OperationResponseCustomizer} bean.
     * <p>
     * This is the core orchestrator component that modifies the OpenAPI {@link io.swagger.v3.oas.models.Operation}
     * object. It cleans up default responses, applies the correct success status, and injects
     * standard error responses.
     *
     * @param responseCodeResolver the component that determines the success status code
     * @param responseFactory      the component that builds the standardized API responses
     * @return a new instance of {@link OperationResponseCustomizer}
     *
     * @author Mateus Silva
     * @since 2.0.0
     */
    @Bean
    @ConditionalOnMissingBean(name = "operationResponseCustomizer")
    public OperationResponseCustomizer operationResponseCustomizer(ResponseCodeResolver responseCodeResolver, ResponseFactory responseFactory) {
        return new OperationResponseCustomizer(responseCodeResolver, responseFactory);
    }

    /**
     * Conditionally registers global OpenAPI security schema contracts if property
     * {@code docflow.security.enabled} is flagged as true.
     *
     * @param properties configuration context engine mapping
     * @return the operational OpenAPI customizer handler
     *
     * @author Mateus Silva
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnProperty(name = "docflow.security.enabled", havingValue = "true")
    public OpenApiCustomizer securityOpenApiCustomizer(DocflowProperties properties) {
        return new SecurityOpenApiCustomizer(properties);
    }

    /**
     * Conditionally registers method-level operational security customizers if property
     * {@code docflow.security.enabled} is flagged as true to draw security locks dynamically.
     *
     * @param properties configuration context engine mapping
     * @return the operational operation customizer handler
     *
     * @author Mateus Silva
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnProperty(name = "docflow.security.enabled", havingValue = "true")
    public OperationCustomizer securityOperationCustomizer(DocflowProperties properties) {
        return new SecurityOperationCustomizer(properties);
    }

    /**
     * Fallback operation registering a fresh baseline OpenAPI documentation metadata object
     * if no consumer implementation bean exists within the context hierarchy.
     *
     * <p>This automatic fallback infers the application name from environment flags and attempts
     * to automatically intercept version numbers from application builds metadata maps.</p>
     *
     * @param properties active configuration context engine mapping
     * @param env global environment properties map for context resolution fallback
     * @param buildProperties optional runtime build properties captured from meta-inf archives
     * @return a structured core OpenAPI metadata handler bean
     *
     * @author Mateus Silva
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(OpenAPI.class)
    @ConditionalOnClass(name = "io.swagger.v3.oas.models.OpenAPI")
    public OpenAPI docflowOpenApi(DocflowProperties properties, Environment env, Optional<BuildProperties> buildProperties) {

        String finalTitle = properties.getTitle();
        if (!StringUtils.hasText(finalTitle)) {
            finalTitle = env.getProperty("spring.application.name");

            if (StringUtils.hasText(finalTitle)) {
                finalTitle = formatTitle(finalTitle);
            } else {
                finalTitle = "API Documentation";
            }
        }

        String finalVersion = properties.getVersion();
        if (!StringUtils.hasText(finalVersion) || "1.0.0".equals(finalVersion)) {

            if (buildProperties.isPresent() && StringUtils.hasText(buildProperties.get().getVersion())) {
                finalVersion = buildProperties.get().getVersion();
            } else if (!StringUtils.hasText(finalVersion)) {
                finalVersion = "1.0.0";
            }
        }

        return new OpenAPI()
                .info(new Info()
                        .title(finalTitle)
                        .description(properties.getDescription())
                        .version(finalVersion));
    }

    @Bean
    public DocFlowSchemaNameConverter docFlowSchemaNameConverter() {
        return new DocFlowSchemaNameConverter();
    }

    /**
     * Formats kebab-case or hyphenated spring boot application naming values into proper
     * Capitalized Titles for enhanced presentation layouts.
     *
     * @param rawName the raw application identifier key string
     * @return a cleanly spaced capitalized text sequence
     *
     * @author Mateus Silva
     * @since 1.0.0
     */
    private String formatTitle(String rawName) {
        String[] words = rawName.replace("-", " ").split(" ");
        for (int i = 0; i < words.length; i++) {
            words[i] = StringUtils.capitalize(words[i]);
        }
        return String.join(" ", words);
    }

}
