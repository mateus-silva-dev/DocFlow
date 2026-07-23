package io.github.mateussilva.docflow.internal.customizer;

import io.github.docflowlib.docflow.internal.customizer.OperationResponseCustomizer;
import io.github.docflowlib.docflow.internal.response.ResponseCodeResolver;
import io.github.docflowlib.docflow.internal.response.ResponseFactory;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OperationResponseCustomizerTest {

    private final ResponseCodeResolver responseCodeResolver = mock(ResponseCodeResolver.class);
    private final ResponseFactory responseFactory = mock(ResponseFactory.class);
    private final OperationResponseCustomizer customizer =
            new OperationResponseCustomizer(responseCodeResolver, responseFactory);

    @AfterEach
    void resetLocale() {
        LocaleContextHolder.resetLocaleContext();
    }

    @Test
    void shouldCreateResponseContainerWhenOperationHasNone() throws NoSuchMethodException {
        HandlerMethod handlerMethod = handlerMethod("findUser");
        when(responseCodeResolver.resolve(handlerMethod)).thenReturn(List.of("200"));
        when(responseFactory.create(eq("200"), eq(null), eq(null), eq(Locale.US)))
                .thenReturn(new ApiResponse().description("OK"));
        LocaleContextHolder.setLocale(Locale.US);

        Operation operation = customizer.customize(new Operation(), handlerMethod);

        assertNotNull(operation.getResponses());
        assertEquals("OK", operation.getResponses().get("200").getDescription());
    }

    @Test
    void shouldRemoveUnexpectedResponsesAndReuseSuccessContent() throws NoSuchMethodException {
        HandlerMethod handlerMethod = handlerMethod("findUser");
        Content successContent = new Content();
        ApiResponse existingSuccess = new ApiResponse().content(successContent);
        ApiResponse createdSuccess = new ApiResponse().description("OK").content(successContent);
        ApiResponse createdError = new ApiResponse().description("Not found");
        ApiResponses responses = new ApiResponses()
                .addApiResponse("200", existingSuccess)
                .addApiResponse("500", new ApiResponse().description("Unexpected"));
        Operation operation = new Operation().responses(responses);

        when(responseCodeResolver.resolve(handlerMethod)).thenReturn(List.of("200", "404"));
        when(responseFactory.create(eq("200"), same(existingSuccess), same(successContent), eq(Locale.CANADA)))
                .thenReturn(createdSuccess);
        when(responseFactory.create(eq("404"), eq(null), same(successContent), eq(Locale.CANADA)))
                .thenReturn(createdError);
        LocaleContextHolder.setLocale(Locale.CANADA);

        Operation customized = customizer.customize(operation, handlerMethod);

        assertSame(operation, customized);
        assertEquals(2, responses.size());
        assertSame(createdSuccess, responses.get("200"));
        assertSame(createdError, responses.get("404"));
        assertFalse(responses.containsKey("500"));
        verify(responseFactory).create("404", null, successContent, Locale.CANADA);
    }

    private HandlerMethod handlerMethod(String methodName) throws NoSuchMethodException {
        Method method = Controller.class.getDeclaredMethod(methodName);
        return new HandlerMethod(new Controller(), method);
    }

    private static class Controller {

        @GetMapping("/users/{id}")
        String findUser() {
            return "user";
        }
    }
}
