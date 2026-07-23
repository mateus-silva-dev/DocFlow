package io.github.mateussilva.docflow.internal.customizer;

import io.github.docflowlib.docflow.internal.customizer.DocFlowSchemaNameConverter;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

class DocFlowSchemaNameConverterTest {

    private final DocFlowSchemaNameConverter converter = new DocFlowSchemaNameConverter();
    private final ModelConverterContext context = mock(ModelConverterContext.class);

    @Test
    void shouldReturnNullWhenConverterChainIsEmpty() {
        Schema<?> schema = converter.resolve(
                new AnnotatedType(String.class),
                context,
                Collections.emptyIterator()
        );

        assertNull(schema);
    }

    @Test
    void shouldPreserveTopLevelSchemaName() {
        Schema<?> schema = converter.resolve(
                new AnnotatedType(TopLevelSchema.class),
                context,
                chainReturning(new Schema<>().name("TopLevelSchema"))
        );

        assertEquals("TopLevelSchema", schema.getName());
    }

    @Test
    void shouldFlattenNestedClassNames() {
        Schema<?> schema = converter.resolve(
                new AnnotatedType(OuterSchema.Middle.Inner.class),
                context,
                chainReturning(new Schema<>().name("Inner"))
        );

        assertEquals("OuterSchemaMiddleInner", schema.getName());
    }

    private Iterator<ModelConverter> chainReturning(Schema<?> schema) {
        ModelConverter converter = (type, context, chain) -> schema;
        return List.of(converter).iterator();
    }
}

class TopLevelSchema {
}

class OuterSchema {
    static class Middle {
        static class Inner {
        }
    }
}
