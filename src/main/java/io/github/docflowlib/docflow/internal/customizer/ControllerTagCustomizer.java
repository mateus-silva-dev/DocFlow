package io.github.docflowlib.docflow.internal.customizer;

import io.github.docflowlib.docflow.annotations.ApiDocController;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

public class ControllerTagCustomizer implements OperationCustomizer, OpenApiCustomizer {

    private final ApplicationContext applicationContext;
    private final MessageSource messageSource;

    public ControllerTagCustomizer(ApplicationContext applicationContext, MessageSource messageSource) {
        this.applicationContext = applicationContext;
        this.messageSource = messageSource;
    }

    @Override
    public void customise(OpenAPI openApi) {
        Map<String, Object> controllers = applicationContext.getBeansWithAnnotation(ApiDocController.class);
        Locale locale = LocaleContextHolder.getLocale();

        for (Object controller : controllers.values()) {
            Class<?> realClass = ClassUtils.getUserClass(controller.getClass());
            ApiDocController annotation = realClass.getAnnotation(ApiDocController.class);
            if (annotation != null) {
                String finalName = resolveTagName(realClass, annotation, locale);

                Tag tag = new Tag().name(finalName);
                if (StringUtils.hasText(annotation.tagDescription())) {
                    String desc = messageSource.getMessage(annotation.tagDescription(), null, annotation.tagDescription(), locale);
                    tag.description(desc);
                }
                openApi.addTagsItem(tag);
            }
        }
    }

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        Class<?> realClass = handlerMethod.getBeanType();
        ApiDocController annotation = realClass.getAnnotation(ApiDocController.class);
        Locale locale = LocaleContextHolder.getLocale();

        if (annotation != null) {
            String finalName = resolveTagName(realClass, annotation, locale);
            operation.setTags(Collections.singletonList(finalName));
        }
        return operation;
    }

    private String resolveTagName(Class<?> controllerClass, ApiDocController annotation, Locale locale) {
        if (StringUtils.hasText(annotation.tagName())) {
            return messageSource.getMessage(annotation.tagName(), null, annotation.tagName(), locale);
        }

        String className = controllerClass.getSimpleName();
        className = className.replace("Controller", "").replace("RestController", "");
        String[] words = className.split("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])");
        String fallbackName = String.join(" ", words);

        return messageSource.getMessage(controllerClass.getSimpleName(), null, fallbackName, locale);
    }

}
