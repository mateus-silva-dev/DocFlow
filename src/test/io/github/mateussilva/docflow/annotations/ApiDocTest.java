package io.github.mateussilva.docflow.annotations;

import io.github.docflowlib.docflow.annotations.ApiDoc;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiDocTest {

    @Test
    void shouldBeRuntimeMethodAnnotation() {
        Target target = ApiDoc.class.getAnnotation(Target.class);
        Retention retention = ApiDoc.class.getAnnotation(Retention.class);

        assertArrayEquals(new ElementType[]{ElementType.METHOD}, target.value());
        assertEquals(RetentionPolicy.RUNTIME, retention.value());
    }

    @Test
    void shouldExposeDefaultAndConfiguredExclusions() throws NoSuchMethodException {
        Method defaultMethod = Controller.class.getDeclaredMethod("defaultDoc");
        Method configuredMethod = Controller.class.getDeclaredMethod("configuredDoc");

        assertArrayEquals(new HttpStatus[]{}, defaultMethod.getAnnotation(ApiDoc.class).exclude());
        assertArrayEquals(
                new HttpStatus[]{HttpStatus.NOT_FOUND, HttpStatus.INTERNAL_SERVER_ERROR},
                configuredMethod.getAnnotation(ApiDoc.class).exclude()
        );
    }

    private static class Controller {

        @ApiDoc
        void defaultDoc() {
        }

        @ApiDoc(exclude = {HttpStatus.NOT_FOUND, HttpStatus.INTERNAL_SERVER_ERROR})
        void configuredDoc() {
        }
    }
}
