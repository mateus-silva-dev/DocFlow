package io.github.docflowlib.docflow.internal.customizer;

import io.github.docflowlib.docflow.annotations.*;
import io.github.docflowlib.docflow.properties.DocflowProperties;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.method.HandlerMethod;

import java.util.Locale;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class ErrorSchemaCustomizer implements OperationCustomizer {

    private final DocflowProperties properties;
    private final MessageSource messageSource;

    public ErrorSchemaCustomizer(DocflowProperties properties, MessageSource messageSource) {
        this.properties = properties;
        this.messageSource = messageSource;
    }

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        ApiDocGet apiDocGet = handlerMethod.getMethodAnnotation(ApiDocGet.class);
        ApiDocPost apiDocPost = handlerMethod.getMethodAnnotation(ApiDocPost.class);
        ApiDocPut apiDocPut = handlerMethod.getMethodAnnotation(ApiDocPut.class);
        ApiDocPatch apiDocPatch = handlerMethod.getMethodAnnotation(ApiDocPatch.class);
        ApiDocDelete apiDocDelete = handlerMethod.getMethodAnnotation(ApiDocDelete.class);

        Class<?> errorClassFromAnnotation = null;
        boolean isDocflowEndpoint = false;

        if (apiDocGet != null) {
            errorClassFromAnnotation = apiDocGet.errorSchema();
            isDocflowEndpoint = true;
        } else if (apiDocPost != null) {
            errorClassFromAnnotation = apiDocPost.errorSchema();
            isDocflowEndpoint = true;
        } else if (apiDocPut != null) {
            errorClassFromAnnotation = apiDocPut.errorSchema();
            isDocflowEndpoint = true;
        } else if (apiDocPatch != null) {
            errorClassFromAnnotation = apiDocPatch.errorSchema();
            isDocflowEndpoint = true;
        } else if (apiDocDelete != null) {
            errorClassFromAnnotation = apiDocDelete.errorSchema();
            isDocflowEndpoint = true;
        }

        if (isDocflowEndpoint) {
            Locale locale = LocaleContextHolder.getLocale();

            operation.getResponses().forEach((httpCode, apiResponse) -> {
                if (apiResponse.getDescription() != null) {
                    String originalKey = apiResponse.getDescription().trim();
                    String translatedDesc = messageSource.getMessage(originalKey, null, originalKey, locale);
                    apiResponse.setDescription(translatedDesc);
                }
            });

            Class<?> errorClassToUse = null;

            if (errorClassFromAnnotation != null && errorClassFromAnnotation != Void.class) {
                errorClassToUse = errorClassFromAnnotation;
            } else if (properties.getDefaultErrorSchema() != null && !properties.getDefaultErrorSchema().isBlank()) {
                try {
                    errorClassToUse = Class.forName(properties.getDefaultErrorSchema());
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("Docflow: Standard error class not found - " + properties.getDefaultErrorSchema(), e);
                }
            }

            if (errorClassToUse != null) {
                ResolvedSchema resolvedSchema = ModelConverters.getInstance()
                        .resolveAsResolvedSchema(new AnnotatedType(errorClassToUse));

                Content errorContent = new Content().addMediaType(
                        APPLICATION_JSON_VALUE, new MediaType().schema(resolvedSchema.schema));

                operation.getResponses().forEach((httpCode, apiResponse) -> {
                    if (httpCode.startsWith("4") || httpCode.startsWith("5")) {
                        apiResponse.setContent(errorContent);
                    }
                });
            }
        }
        return operation;
    }

}
