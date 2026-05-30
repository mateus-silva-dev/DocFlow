package io.github.docflowlib.docflow.internal.customizer;

import io.github.docflowlib.docflow.annotations.ApiDocDelete;
import io.github.docflowlib.docflow.annotations.ApiDocPatch;
import io.github.docflowlib.docflow.annotations.ApiDocPut;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.method.HandlerMethod;

import java.util.Locale;

public class DynamicSuccessCustomizer implements OperationCustomizer {

    private final MessageSource messageSource;

    public DynamicSuccessCustomizer(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        ApiDocPatch apiDocPatch = handlerMethod.getMethodAnnotation(ApiDocPatch.class);
        ApiDocPut apiDocPut = handlerMethod.getMethodAnnotation(ApiDocPut.class);
        ApiDocDelete apiDocDelete = handlerMethod.getMethodAnnotation(ApiDocDelete.class);

        Boolean hasBody = null;
        String actionKey = "";
        String defaultAction = "";

        if (apiDocPatch != null) {
            hasBody = apiDocPatch.withBody();
            actionKey = "docflow.action.updated";
            defaultAction = "The feature was successfully updated.";
        } else if (apiDocPut != null) {
            hasBody = apiDocPut.withBody();
            actionKey = "docflow.action.updated";
            defaultAction = "The feature was successfully updated.";
        } else if (apiDocDelete != null) {
            hasBody = apiDocDelete.withBody();
            actionKey = "docflow.action.deleted";
            defaultAction = "The feature was successfully deleted.";
        }

        if (hasBody != null) {
            ApiResponse successResponse = new ApiResponse();
            Locale locale = LocaleContextHolder.getLocale();

            String translatedAction = messageSource.getMessage(actionKey, null, defaultAction, locale);

            if (hasBody) {
                successResponse.description(translatedAction);
                operation.getResponses().addApiResponse("200", successResponse);
            } else {
                String translatedNoContent = messageSource.getMessage("docflow.action.nocontent", null, " No content returned.", locale);
                successResponse.description(translatedAction + " " + translatedNoContent);
                operation.getResponses().addApiResponse("204", successResponse);
            }
        }

        return operation;
    }
}
