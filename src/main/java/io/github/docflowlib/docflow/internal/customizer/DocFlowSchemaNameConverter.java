package io.github.docflowlib.docflow.internal.customizer;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.oas.models.media.Schema;
import org.springframework.context.annotation.Configuration;

import java.util.Iterator;

@Configuration
public class DocFlowSchemaNameConverter implements ModelConverter {

    @Override
    public Schema<?> resolve(AnnotatedType type, ModelConverterContext context, Iterator<ModelConverter> chain) {
        Schema<?> schema = null;
        if (chain.hasNext()) {
            schema = chain.next().resolve(type, context, chain);
        }

        if (schema != null && type.getType() instanceof Class<?> clazz) {
            if (clazz.getEnclosingClass() != null) {
                schema.setName(resolveSchemaName(clazz));
            }
        }

        return schema;
    }

    private String resolveSchemaName(Class<?> type) {
        StringBuilder schemaName = new StringBuilder(type.getSimpleName());
        Class<?> enclosingClass = type.getEnclosingClass();

        while (enclosingClass != null) {
            schemaName.insert(0, enclosingClass.getSimpleName());
            enclosingClass = enclosingClass.getEnclosingClass();
        }

        return schemaName.toString();
    }
}
