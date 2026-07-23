package io.github.mateussilva.docflow.internal.customizer;

import io.github.docflowlib.docflow.internal.customizer.SecurityOperationCustomizer;
import io.github.docflowlib.docflow.properties.DocflowProperties;
import io.swagger.v3.oas.models.Operation;
import org.junit.jupiter.api.Test;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SecurityOperationCustomizerTest {

    @Test
    void shouldNotAddSecurityWhenDisabled() throws NoSuchMethodException {
        DocflowProperties properties = new DocflowProperties();
        SecurityOperationCustomizer customizer = new SecurityOperationCustomizer(properties);

        Operation operation = customizer.customize(new Operation(), handlerMethod(new MethodSecuredController(), "secured"));

        assertNull(operation.getSecurity());
    }

    @Test
    void shouldAddSecurityWhenMethodHasSupportedSecurityAnnotation() throws NoSuchMethodException {
        DocflowProperties properties = enabledProperties();
        SecurityOperationCustomizer customizer = new SecurityOperationCustomizer(properties);

        Operation operation = customizer.customize(new Operation(), handlerMethod(new MethodSecuredController(), "secured"));

        assertEquals(1, operation.getSecurity().size());
        assertEquals(true, operation.getSecurity().get(0).containsKey("bearerAuth"));
    }

    @Test
    void shouldAddSecurityWhenControllerHasSupportedSecurityAnnotation() throws NoSuchMethodException {
        SecurityOperationCustomizer customizer = new SecurityOperationCustomizer(enabledProperties());

        Operation operation = customizer.customize(new Operation(), handlerMethod(new ClassSecuredController(), "securedByClass"));

        assertEquals(1, operation.getSecurity().size());
    }

    @Test
    void shouldLeaveOperationUnsecuredWhenNoSupportedAnnotationExists() throws NoSuchMethodException {
        SecurityOperationCustomizer customizer = new SecurityOperationCustomizer(enabledProperties());

        Operation operation = customizer.customize(new Operation(), handlerMethod(new PublicController(), "publicEndpoint"));

        assertNull(operation.getSecurity());
    }

    private DocflowProperties enabledProperties() {
        DocflowProperties properties = new DocflowProperties();
        properties.getSecurity().setEnabled(true);
        return properties;
    }

    private HandlerMethod handlerMethod(Object controller, String methodName) throws NoSuchMethodException {
        Method method = controller.getClass().getDeclaredMethod(methodName);
        return new HandlerMethod(controller, method);
    }

    @Retention(RetentionPolicy.RUNTIME)
    private @interface PreAuthorize {
    }

    @Retention(RetentionPolicy.RUNTIME)
    private @interface RolesAllowed {
    }

    private static class MethodSecuredController {

        @PreAuthorize
        void secured() {
        }
    }

    @RolesAllowed
    private static class ClassSecuredController {

        void securedByClass() {
        }
    }

    private static class PublicController {

        void publicEndpoint() {
        }
    }
}
