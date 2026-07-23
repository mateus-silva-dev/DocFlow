package io.github.docflowlib.docflow.internal.customizer;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.Operation;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;

import java.util.List;
import java.util.Locale;

@Order(100)
public class OperationTextCustomizer implements OperationCustomizer {

    private final MessageSource messageSource;

    public OperationTextCustomizer(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        Locale locale = LocaleContextHolder.getLocale();

        customizeTag(operation, handlerMethod, locale);
        customizeSummary(operation, handlerMethod, locale);
        customizeDescription(operation, locale);

        return operation;
    }

    private void customizeTag(Operation operation, HandlerMethod handlerMethod, Locale locale) {
        Class<?> controllerClass = handlerMethod.getBeanType();

        if (AnnotatedElementUtils.hasAnnotation(controllerClass, Tag.class)) {
            return;
        }

        String controllerName = controllerClass.getSimpleName()
                .replace("RestController", "")
                .replace("Controller", "");

        String fallback = humanize(controllerName);

        String tag = messageSource.getMessage(controllerName, null, fallback, locale);

        operation.setTags(List.of(tag));
    }

    private void customizeSummary(Operation operation, HandlerMethod handlerMethod, Locale locale) {
        if (StringUtils.hasText(operation.getSummary())) {
            return;
        }

        String methodName = handlerMethod.getMethod().getName();
        String fallback = humanize(methodName);

        String summary = messageSource.getMessage(methodName, null, fallback, locale);

        operation.setSummary(summary);
    }

    private void customizeDescription(Operation operation, Locale locale) {
        if (StringUtils.hasText(operation.getDescription())) {
            return;
        }

        String fallback = "Endpoint responsible for " + operation.getSummary();

        String description = messageSource.getMessage(
                "docflow.auto.description", new Object[]{operation.getSummary()}, fallback, locale);

        operation.setDescription(description);
    }

    private String humanize(String value) {
        String[] words = value.split("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])");

        return StringUtils.capitalize(String.join(" ", words));
    }
}
