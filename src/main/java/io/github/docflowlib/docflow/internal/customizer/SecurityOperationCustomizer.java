package io.github.docflowlib.docflow.internal.customizer;

import io.github.docflowlib.docflow.properties.DocflowProperties;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class SecurityOperationCustomizer implements OperationCustomizer {

    private final DocflowProperties properties;

    public SecurityOperationCustomizer(DocflowProperties properties) {
        this.properties = properties;
    }

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        if (!properties.getSecurity().isEnabled()) return operation;

        Method method = handlerMethod.getMethod();

        boolean hasSecurityAnnotation = isSecured(method);

        if (hasSecurityAnnotation) {
            operation.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
        }

        return operation;
    }

    private boolean isSecured(Method method) {
        Class<?> controller = method.getDeclaringClass();
        return hasSecurityAnnotation(method.getAnnotations())
                || hasSecurityAnnotation(controller.getAnnotations());
    }

    private boolean hasSecurityAnnotation(Annotation[] annotations) {
        if (annotations == null) return false;

        for (Annotation annotation : annotations) {
            String annotationName = annotation.annotationType().getSimpleName();
            if (annotationName.equals("PreAuthorize")
                    || annotationName.equals("Secured")
                    || annotationName.equals("RolesAllowed")) {
                return true;
            }
        }
        return false;
    }
}
