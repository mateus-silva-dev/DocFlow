package io.github.mateussilva.docflow.internal.response;

import io.github.docflowlib.docflow.annotations.ApiDoc;
import io.github.docflowlib.docflow.internal.response.ResponseCodeResolver;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResponseCodeResolverTest {

    private final ResponseCodeResolver resolver = new ResponseCodeResolver();

    @Test
    void shouldResolveGetMappingStandardCodes() throws NoSuchMethodException {
        assertEquals(
                List.of("200", "401", "403", "404", "500"),
                resolver.resolve(handlerMethod("findUser"))
        );
    }

    @Test
    void shouldResolvePostMappingStandardCodes() throws NoSuchMethodException {
        assertEquals(
                List.of("201", "400", "401", "403", "409", "422", "500"),
                resolver.resolve(handlerMethod("createUser"))
        );
    }

    @Test
    void shouldResolvePutAndPatchMappingStandardCodes() throws NoSuchMethodException {
        assertEquals(
                List.of("200", "400", "401", "403", "404", "422", "500"),
                resolver.resolve(handlerMethod("replaceUser"))
        );
        assertEquals(
                List.of("200", "400", "401", "403", "404", "422", "500"),
                resolver.resolve(handlerMethod("updateUser"))
        );
    }

    @Test
    void shouldResolveDeleteMappingAsNoContent() throws NoSuchMethodException {
        assertEquals(
                List.of("204", "401", "403", "404", "500"),
                resolver.resolve(handlerMethod("deleteUser"))
        );
    }

    @Test
    void shouldUseTwoHundredResponseStatusWhenConfigured() throws NoSuchMethodException {
        assertEquals(
                List.of("202", "400", "401", "403", "409", "422", "500"),
                resolver.resolve(handlerMethod("enqueueUserCreation"))
        );
    }

    @Test
    void shouldApplyApiDocExclusions() throws NoSuchMethodException {
        assertEquals(
                List.of("204", "401", "500"),
                resolver.resolve(handlerMethod("deleteUserWithExclusions"))
        );
    }

    @Test
    void shouldFallbackToNoContentForVoidResponses() throws NoSuchMethodException {
        assertEquals(List.of("204"), resolver.resolve(handlerMethod("plainVoid")));
        assertEquals(List.of("204"), resolver.resolve(handlerMethod("plainResponseEntityVoid")));
    }

    @Test
    void shouldFallbackToOkForObjectResponses() throws NoSuchMethodException {
        assertEquals(List.of("200"), resolver.resolve(handlerMethod("plainObject")));
    }

    private HandlerMethod handlerMethod(String methodName) throws NoSuchMethodException {
        Method method = Controller.class.getDeclaredMethod(methodName);
        return new HandlerMethod(new Controller(), method);
    }

    private static class Controller {

        @GetMapping("/users/{id}")
        String findUser() {
            return "user";
        }

        @PostMapping("/users")
        String createUser() {
            return "created";
        }

        @PutMapping("/users/{id}")
        String replaceUser() {
            return "replaced";
        }

        @PatchMapping("/users/{id}")
        String updateUser() {
            return "updated";
        }

        @DeleteMapping("/users/{id}")
        void deleteUser() {
        }

        @PostMapping("/users/import")
        @ResponseStatus(HttpStatus.ACCEPTED)
        String enqueueUserCreation() {
            return "accepted";
        }

        @DeleteMapping("/users/{id}/filtered")
        @ApiDoc(exclude = {HttpStatus.NOT_FOUND, HttpStatus.FORBIDDEN})
        void deleteUserWithExclusions() {
        }

        void plainVoid() {
        }

        ResponseEntity<Void> plainResponseEntityVoid() {
            return ResponseEntity.noContent().build();
        }

        String plainObject() {
            return "plain";
        }
    }
}
