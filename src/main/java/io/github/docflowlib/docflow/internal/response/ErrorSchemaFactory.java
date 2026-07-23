package io.github.docflowlib.docflow.internal.response;

import io.github.docflowlib.docflow.properties.DocflowProperties;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.models.media.Content;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

public class ErrorSchemaFactory {

    private final DocflowProperties docflowProperties;

    public ErrorSchemaFactory(
            DocflowProperties docflowProperties
    ) {
        this.docflowProperties = docflowProperties;
    }

    public Content create() {
        String errorClassFqn =
                docflowProperties.getDefaultErrorSchema();

        if (!StringUtils.hasText(errorClassFqn)) {
            return null;
        }

        try {
            Class<?> errorClass =
                    Class.forName(errorClassFqn);

            ResolvedSchema resolvedSchema =
                    ModelConverters.getInstance()
                            .resolveAsResolvedSchema(
                                    new AnnotatedType(errorClass)
                            );

            if (resolvedSchema.schema == null) {
                return null;
            }

            return new Content().addMediaType(
                    MediaType.APPLICATION_JSON_VALUE,
                    new io.swagger.v3.oas.models.media.MediaType()
                            .schema(resolvedSchema.schema)
            );

        } catch (ClassNotFoundException exception) {
            throw new IllegalStateException(
                    "DocFlow: standard error class not found: "
                            + errorClassFqn,
                    exception
            );
        }
    }
}