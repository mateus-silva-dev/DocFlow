package io.github.docflowlib.docflow.internal.config;

import io.github.docflowlib.docflow.internal.customizer.*;
import io.github.docflowlib.docflow.internal.customizer.AutoSummaryCustomizer;
import io.github.docflowlib.docflow.internal.customizer.ControllerTagCustomizer;
import io.github.docflowlib.docflow.internal.customizer.DynamicSuccessCustomizer;
import io.github.docflowlib.docflow.internal.customizer.ErrorSchemaCustomizer;
import io.github.docflowlib.docflow.internal.customizer.SecurityOpenApiCustomizer;
import io.github.docflowlib.docflow.properties.DocflowProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.ApplicationContext;
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
     */
    @Bean
    public MessageSource docflowMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("docflow-messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    /**
     * Registers the customizer responsible for injecting dynamic HTTP success schemas (200, 201, 204)
     * based on return types and metadata annotations.
     *
     * @param messageSource the isolated internal message source for localization contracts
     * @return the operational customizer instance
     */
    @Bean
    public DynamicSuccessCustomizer patchSuccessCustomizer(@Qualifier("docflowMessageSource") MessageSource messageSource) {
        return new DynamicSuccessCustomizer(messageSource);
    }

    /**
     * Registers the customizer engine designed to inject standard global error schemas
     * across all active endpoint mappings.
     *
     * @param properties corporate environment properties mapping
     * @param messageSource the isolated internal message source for localization contracts
     * @return the operational customizer instance
     */
    @Bean
    public ErrorSchemaCustomizer errorSchemaCustomizer(DocflowProperties properties,
                                                       @Qualifier("docflowMessageSource") MessageSource messageSource) {
        return new ErrorSchemaCustomizer(properties, messageSource);
    }

    /**
     * Registers the structural operation customizer built to automate swagger summaries
     * parsing from endpoints methods reflections.
     *
     * @param messageSource the isolated internal message source for localization contracts
     * @return the operational customizer instance
     */
    @Bean
    public AutoSummaryCustomizer autoSummaryCustomizer(@Qualifier("docflowMessageSource") MessageSource messageSource) {
        return new AutoSummaryCustomizer(messageSource);
    }

    /**
     * Registers the tag organization processor built to aggregate swagger group targets
     * clean using class reflection architectures.
     *
     * @param context the application context for resource validation
     * @param messageSource the isolated internal message source for localization contracts
     * @return the operational customizer instance
     */
    @Bean
    public ControllerTagCustomizer controllerTagCustomizer(ApplicationContext context,
                                                           @Qualifier("docflowMessageSource") MessageSource messageSource) {
        return new ControllerTagCustomizer(context, messageSource);
    }

    /**
     * Conditionally registers global OpenAPI security schema contracts if property
     * {@code docflow.security.enabled} is flagged as true.
     *
     * @param properties configuration context engine mapping
     * @return the operational OpenAPI customizer handler
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
     */
    @Bean
    @ConditionalOnMissingBean(OpenAPI.class)
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

    /**
     * Formats kebab-case or hyphenated spring boot application naming values into proper
     * Capitalized Titles for enhanced presentation layouts.
     *
     * @param rawName the raw application identifier key string
     * @return a cleanly spaced capitalized text sequence
     */
    private String formatTitle(String rawName) {
        String[] words = rawName.replace("-", " ").split(" ");
        for (int i = 0; i < words.length; i++) {
            words[i] = StringUtils.capitalize(words[i]);
        }
        return String.join(" ", words);
    }

}
