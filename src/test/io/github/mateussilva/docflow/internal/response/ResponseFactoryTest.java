package io.github.mateussilva.docflow.internal.response;

import io.github.docflowlib.docflow.internal.response.ErrorSchemaFactory;
import io.github.docflowlib.docflow.internal.response.ResponseFactory;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ResponseFactoryTest {

    private final MessageSource messageSource = mock(MessageSource.class);
    private final ErrorSchemaFactory errorSchemaFactory = mock(ErrorSchemaFactory.class);
    private final ResponseFactory responseFactory = new ResponseFactory(messageSource, errorSchemaFactory);

    @Test
    void shouldCreateSuccessResponseWithLocalizedDescriptionAndSuccessContent() {
        Content successContent = new Content();
        when(messageSource.getMessage(eq("docflow.codes.200"), isNull(), eq("Response 200"), eq(Locale.US)))
                .thenReturn("Successful response");

        ApiResponse response = responseFactory.create("200", null, successContent, Locale.US);

        assertEquals("Successful response", response.getDescription());
        assertSame(successContent, response.getContent());
        verify(errorSchemaFactory, never()).create();
    }

    @Test
    void shouldNotOverwriteExistingSuccessContent() {
        Content existingContent = new Content();
        ApiResponse existingResponse = new ApiResponse().content(existingContent);
        when(messageSource.getMessage(eq("docflow.codes.201"), isNull(), eq("Response 201"), eq(Locale.US)))
                .thenReturn("Created");

        ApiResponse response = responseFactory.create("201", existingResponse, new Content(), Locale.US);

        assertSame(existingResponse, response);
        assertEquals("Created", response.getDescription());
        assertSame(existingContent, response.getContent());
    }

    @Test
    void shouldRemoveContentForNoContentResponses() {
        ApiResponse existingResponse = new ApiResponse().content(new Content());
        when(messageSource.getMessage(eq("docflow.codes.204"), isNull(), eq("Response 204"), eq(Locale.US)))
                .thenReturn("No content");

        ApiResponse response = responseFactory.create("204", existingResponse, new Content(), Locale.US);

        assertEquals("No content", response.getDescription());
        assertNull(response.getContent());
    }

    @Test
    void shouldUseErrorSchemaForErrorResponsesWithoutContent() {
        Content errorContent = new Content();
        when(messageSource.getMessage(eq("docflow.codes.404"), isNull(), eq("Response 404"), eq(Locale.US)))
                .thenReturn("Not found");
        when(errorSchemaFactory.create()).thenReturn(errorContent);

        ApiResponse response = responseFactory.create("404", null, new Content(), Locale.US);

        assertEquals("Not found", response.getDescription());
        assertSame(errorContent, response.getContent());
    }

    @Test
    void shouldNotOverwriteExistingErrorContent() {
        Content existingContent = new Content();
        ApiResponse existingResponse = new ApiResponse().content(existingContent);
        when(messageSource.getMessage(eq("docflow.codes.500"), isNull(), eq("Response 500"), eq(Locale.US)))
                .thenReturn("Server error");

        ApiResponse response = responseFactory.create("500", existingResponse, null, Locale.US);

        assertSame(existingResponse, response);
        assertSame(existingContent, response.getContent());
        verify(errorSchemaFactory, never()).create();
    }

    @Test
    void shouldFallbackDescriptionKeyForUnknownStatusCode() {
        when(messageSource.getMessage(eq("docflow.codes.418"), isNull(), eq("Response 418"), eq(Locale.US)))
                .thenReturn("Response 418");

        ApiResponse response = responseFactory.create("418", null, null, Locale.US);

        assertEquals("Response 418", response.getDescription());
    }
}
