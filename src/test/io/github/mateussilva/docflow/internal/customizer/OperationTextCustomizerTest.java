package io.github.mateussilva.docflow.internal.customizer;

import io.github.docflowlib.docflow.internal.customizer.OperationTextCustomizer;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.Operation;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OperationTextCustomizerTest {

    @Test
    void shouldGenerateTagSummaryAndDescriptionFromFallbackText() throws NoSuchMethodException {
        MessageSource messageSource = fallbackMessageSource();
        OperationTextCustomizer customizer = new OperationTextCustomizer(messageSource);
        Operation operation = new Operation();

        customizer.customize(operation, handlerMethod(new UserRestController(), "listActiveUsers"));

        assertEquals(List.of("User"), operation.getTags());
        assertEquals("List Active Users", operation.getSummary());
        assertEquals("Endpoint responsible for List Active Users", operation.getDescription());
    }

    @Test
    void shouldUseLocalizedMessagesWhenAvailable() throws NoSuchMethodException {
        MessageSource messageSource = fallbackMessageSource();
        when(messageSource.getMessage(anyString(), nullable(Object[].class), anyString(), any()))
                .thenAnswer(invocation -> switch ((String) invocation.getArgument(0)) {
                    case "User" -> "Usuarios";
                    case "listActiveUsers" -> "Listar usuarios ativos";
                    case "docflow.auto.description" -> "Descricao localizada";
                    default -> invocation.getArgument(2);
                });
        OperationTextCustomizer customizer = new OperationTextCustomizer(messageSource);
        Operation operation = new Operation();

        customizer.customize(operation, handlerMethod(new UserRestController(), "listActiveUsers"));

        assertEquals(List.of("Usuarios"), operation.getTags());
        assertEquals("Listar usuarios ativos", operation.getSummary());
        assertEquals("Descricao localizada", operation.getDescription());
    }

    @Test
    void shouldPreserveExplicitTagSummaryAndDescription() throws NoSuchMethodException {
        OperationTextCustomizer customizer = new OperationTextCustomizer(fallbackMessageSource());
        Operation operation = new Operation()
                .summary("Explicit summary")
                .description("Explicit description");

        customizer.customize(operation, handlerMethod(new TaggedController(), "findReport"));

        assertNull(operation.getTags());
        assertEquals("Explicit summary", operation.getSummary());
        assertEquals("Explicit description", operation.getDescription());
    }

    private MessageSource fallbackMessageSource() {
        MessageSource messageSource = mock(MessageSource.class);
        when(messageSource.getMessage(anyString(), nullable(Object[].class), anyString(), any()))
                .thenAnswer(invocation -> invocation.getArgument(2));
        return messageSource;
    }

    private HandlerMethod handlerMethod(Object controller, String methodName) throws NoSuchMethodException {
        Method method = controller.getClass().getDeclaredMethod(methodName);
        return new HandlerMethod(controller, method);
    }

    private static class UserRestController {

        void listActiveUsers() {
        }
    }

    @Tag(name = "Reports")
    private static class TaggedController {

        void findReport() {
        }
    }
}
