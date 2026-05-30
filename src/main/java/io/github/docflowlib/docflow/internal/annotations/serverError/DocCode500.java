package io.github.docflowlib.docflow.internal.annotations.serverError;

import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Internal use only.
 * Not part of the public API.
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@ApiResponse(
        responseCode = "500",
        description = "docflow.codes.500"
)
public @interface DocCode500 {
}
