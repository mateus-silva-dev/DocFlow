package io.github.docflowlib.docflow.internal.customizer;

import io.github.docflowlib.docflow.internal.response.ResponseCodeResolver;
import io.github.docflowlib.docflow.internal.response.ResponseFactory;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.web.method.HandlerMethod;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Order(200)
public class OperationResponseCustomizer implements OperationCustomizer {

    private final ResponseCodeResolver responseCodeResolver;
    private final ResponseFactory responseFactory;

    public OperationResponseCustomizer(ResponseCodeResolver responseCodeResolver, ResponseFactory responseFactory) {
        this.responseCodeResolver = responseCodeResolver;
        this.responseFactory = responseFactory;
    }

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        ApiResponses responses = operation.getResponses();

        if (responses == null) {
            responses = new ApiResponses();
            operation.setResponses(responses);
        }

        Content successContent = findSuccessContent(responses);

        List<String> codes = responseCodeResolver.resolve(handlerMethod);

        removeUnexpectedResponses(responses, codes);

        Locale locale = LocaleContextHolder.getLocale();

        for (String code : codes) {
            ApiResponse response = responseFactory.create(
                    code, responses.get(code), successContent, locale);

            responses.addApiResponse(code, response);
        }

        return operation;
    }

    private Content findSuccessContent(ApiResponses responses) {
        for (String code : List.of("200", "201", "202")) {
            ApiResponse response = responses.get(code);

            if (response != null && response.getContent() != null) {
                return response.getContent();
            }
        }

        return null;
    }

    private void removeUnexpectedResponses(ApiResponses responses, List<String> expectedCodes) {
        Set<String> nativeCodes = new HashSet<>(responses.keySet());

        for (String nativeCode : nativeCodes) {
            if (!expectedCodes.contains(nativeCode)) {
                responses.remove(nativeCode);
            }
        }
    }
}
