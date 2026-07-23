package io.github.docflowlib.docflow.internal.response;

import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.Map;

public class ResponseFactory {

    private static final Map<String, String> DESCRIPTIONS =
            Map.of(
                    "200", "docflow.codes.200",
                    "201", "docflow.codes.201",
                    "204", "docflow.codes.204",
                    "400", "docflow.codes.400",
                    "401", "docflow.codes.401",
                    "403", "docflow.codes.403",
                    "404", "docflow.codes.404",
                    "409", "docflow.codes.409",
                    "422", "docflow.codes.422",
                    "500", "docflow.codes.500"
            );

    private final MessageSource messageSource;
    private final ErrorSchemaFactory errorSchemaFactory;

    public ResponseFactory(MessageSource messageSource, ErrorSchemaFactory errorSchemaFactory) {
        this.messageSource = messageSource;
        this.errorSchemaFactory = errorSchemaFactory;
    }

    public ApiResponse create(String code, ApiResponse existingResponse, Content successContent, Locale locale) {
        ApiResponse response = existingResponse != null ? existingResponse : new ApiResponse();

        response.setDescription(resolveDescription(code, locale));

        if ("204".equals(code)) {
            response.setContent(null);
            return response;
        }

        if (isSuccessCode(code)) {
            if (response.getContent() == null
                    && successContent != null) {
                response.setContent(successContent);
            }

            return response;
        }

        if (isErrorCode(code)
                && response.getContent() == null) {
            response.setContent(
                    errorSchemaFactory.create()
            );
        }

        return response;
    }

    private String resolveDescription(String code, Locale locale) {
        String propertyKey = DESCRIPTIONS.getOrDefault(code, "docflow.codes." + code);

        return messageSource.getMessage(propertyKey, null, "Response " + code, locale);
    }

    private boolean isSuccessCode(String code) {
        return code.startsWith("2");
    }

    private boolean isErrorCode(String code) {
        return code.startsWith("4") || code.startsWith("5");
    }
}