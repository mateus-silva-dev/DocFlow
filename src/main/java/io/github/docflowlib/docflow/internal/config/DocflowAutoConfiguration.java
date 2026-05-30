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

@AutoConfiguration
@EnableConfigurationProperties(DocflowProperties.class)
public class DocflowAutoConfiguration {

    @Bean
    public MessageSource docflowMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("docflow-messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public DynamicSuccessCustomizer patchSuccessCustomizer(@Qualifier("docflowMessageSource") MessageSource messageSource) {
        return new DynamicSuccessCustomizer(messageSource);
    }

    @Bean
    public ErrorSchemaCustomizer errorSchemaCustomizer(DocflowProperties properties,
                                                       @Qualifier("docflowMessageSource") MessageSource messageSource) {
        return new ErrorSchemaCustomizer(properties, messageSource);
    }

    @Bean
    public AutoSummaryCustomizer autoSummaryCustomizer(@Qualifier("docflowMessageSource") MessageSource messageSource) {
        return new AutoSummaryCustomizer(messageSource);
    }

    @Bean
    public ControllerTagCustomizer controllerTagCustomizer(ApplicationContext context,
                                                           @Qualifier("docflowMessageSource") MessageSource messageSource) {
        return new ControllerTagCustomizer(context, messageSource);
    }

    @Bean
    @ConditionalOnProperty(name = "docflow.security.enabled", havingValue = "true")
    public OpenApiCustomizer securityOpenApiCustomizer(DocflowProperties properties) {
        return new SecurityOpenApiCustomizer(properties);
    }

    @Bean
    @ConditionalOnProperty(name = "docflow.security.enabled", havingValue = "true")
    public OperationCustomizer securityOperationCustomizer(DocflowProperties properties) {
        return new SecurityOperationCustomizer(properties);
    }

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

    private String formatTitle(String rawName) {
        String[] words = rawName.replace("-", " ").split(" ");
        for (int i = 0; i < words.length; i++) {
            words[i] = StringUtils.capitalize(words[i]);
        }
        return String.join(" ", words);
    }

}
