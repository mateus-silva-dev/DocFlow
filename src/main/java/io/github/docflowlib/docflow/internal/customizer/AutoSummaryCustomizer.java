package io.github.docflowlib.docflow.internal.customizer;

import io.swagger.v3.oas.models.Operation;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;

import java.util.Locale;

public class AutoSummaryCustomizer implements OperationCustomizer {

    private final MessageSource messageSource;

    public AutoSummaryCustomizer(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        Locale locale = LocaleContextHolder.getLocale();

        if (!StringUtils.hasText(operation.getSummary())) {
            String methodName = handlerMethod.getMethod().getName();
            String[] words = methodName.split("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])");
            String fallbackSummary = StringUtils.capitalize(String.join(" ", words));
            String finalSummary = messageSource.getMessage(methodName, null, fallbackSummary, locale);
            operation.setSummary(finalSummary);
        } else {
            String finalSummary = messageSource.getMessage(operation.getSummary(), null, operation.getSummary(), locale);
            operation.setSummary(finalSummary);
        }

        if (!StringUtils.hasText(operation.getDescription())) {
            String defaultDesc = "Endpoint responsible for " + operation.getSummary();
            String finalDesc = messageSource.getMessage("docflow.auto.description", new Object[]{operation.getSummary()}, defaultDesc, locale);
            operation.setDescription(finalDesc);
        } else {
            String finalDesc = messageSource.getMessage(operation.getDescription(), null, operation.getDescription(), locale);
            operation.setDescription(finalDesc);
        }

        return operation;
    }
}
