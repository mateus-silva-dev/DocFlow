package io.github.docflowlib.docflow.internal.response;

import io.github.docflowlib.docflow.annotations.ApiDoc;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResponseCodeResolver {

    private static final Map<Class<? extends Annotation>, List<String>> HTTP_STANDARDS = Map.of(
            GetMapping.class, List.of("200", "401", "403", "404", "500"),
            PostMapping.class, List.of("201", "400", "401", "403", "409", "422", "500"),
            PutMapping.class, List.of("200", "400", "401", "403", "404", "422", "500"),
            PatchMapping.class, List.of("200", "400", "401", "403", "404", "422", "500"),
            DeleteMapping.class, List.of("204", "401", "403", "404", "500")
    );

    public List<String> resolve(HandlerMethod handlerMethod) {
        List<String> codes = resolveStandardCodes(handlerMethod);
        String successCode = resolveSuccessCode(handlerMethod);

        codes.removeIf(this::isSuccessCode);

        if (successCode != null) {
            codes.add(0, successCode);
        }

        applyExclusions(handlerMethod, codes);

        return codes;
    }

    private List<String> resolveStandardCodes(HandlerMethod handlerMethod) {
        Method method = handlerMethod.getMethod();

        for (var entry : HTTP_STANDARDS.entrySet()) {
            if (AnnotatedElementUtils.hasAnnotation(method, entry.getKey())) {
                return new ArrayList<>(entry.getValue());
            }
        }

        return new ArrayList<>();
    }

    private String resolveSuccessCode(HandlerMethod handlerMethod) {
        Method method = handlerMethod.getMethod();
        ResponseStatus responseStatus = AnnotatedElementUtils.findMergedAnnotation(method, ResponseStatus.class);

        if (responseStatus != null) {
            HttpStatusCode statusCode = responseStatus.code();
            if (statusCode.is2xxSuccessful()) {
                return String.valueOf(statusCode.value());
            }
        }

        if (AnnotatedElementUtils.hasAnnotation(method, DeleteMapping.class)) {
            return "204";
        }

        if (AnnotatedElementUtils.hasAnnotation(method, PostMapping.class)) {
            return "201";
        }

        if (AnnotatedElementUtils.hasAnnotation(method, PutMapping.class) ||
                AnnotatedElementUtils.hasAnnotation(method, PatchMapping.class) ||
                AnnotatedElementUtils.hasAnnotation(method, GetMapping.class)) {
            return isVoidResponse(handlerMethod) ? "204" : "200";
        }

        return isVoidResponse(handlerMethod) ? "204" : "200";
    }

    private boolean isVoidResponse(HandlerMethod handlerMethod) {
        Method method = handlerMethod.getMethod();
        Class<?> returnType = method.getReturnType();

        if (void.class.equals(returnType) || Void.class.equals(returnType)) {
            return true;
        }

        ResolvableType type = ResolvableType.forMethodReturnType(method);
        return containsVoidGeneric(type);
    }

    private boolean containsVoidGeneric(ResolvableType type) {
        if (type == ResolvableType.NONE) {
            return false;
        }
        if (Void.class.equals(type.resolve())) {
            return true;
        }
        for (ResolvableType generic : type.getGenerics()) {
            if (containsVoidGeneric(generic)) {
                return true;
            }
        }
        return false;
    }

    private void applyExclusions(HandlerMethod handlerMethod, List<String> codes) {
        ApiDoc apiDoc = AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getMethod(), ApiDoc.class);

        if (apiDoc == null) {
            return;
        }

        for (HttpStatus status : apiDoc.exclude()) {
            codes.remove(String.valueOf(status.value()));
        }
    }

    private boolean isSuccessCode(String code) {
        return code.startsWith("2");
    }
}
