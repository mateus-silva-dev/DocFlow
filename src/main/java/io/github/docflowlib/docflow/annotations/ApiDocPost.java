package io.github.docflowlib.docflow.annotations;

import io.github.docflowlib.docflow.internal.annotations.errorClient.*;
import io.github.docflowlib.docflow.internal.annotations.serverError.DocCode500;
import io.github.docflowlib.docflow.internal.annotations.success.DocCode201;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Documents an HTTP POST endpoint using DocFlow automation.
 *
 * <p>DocFlow automatically generates OpenAPI documentation for the
 * annotated operation, including response schemas and standard
 * HTTP status codes for resource creation.</p>
 *
 * <h3>Default Responses</h3>
 * <ul>
 *   <li>201 - Created (Success)</li>
 *   <li>400 - Bad Request</li>
 *   <li>401 - Unauthorized</li>
 *   <li>403 - Forbidden</li>
 *   <li>409 - Conflict</li>
 *   <li>422 - Unprocessable Entity</li>
 *   <li>500 - Internal Server Error</li>
 * </ul>
 *
 * <p>Example usage (Automatic Mode):</p>
 * <pre>{@code
 * @PostMapping
 * @ApiDocPost
 * public ResponseEntity<UserResponse> create(@RequestBody UserRequest request) {
 *     return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
 * }
 * }</pre>
 *
 * <p>Example usage (Custom Mode - Overriding metadata):</p>
 * <pre>{@code
 * @PostMapping("/register")
 * @ApiDocPost(
 *     summary = "Register a new account",
 *     description = "Creates a brand new user profile and triggers validation email"
 * )
 * public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
 *     return ResponseEntity.status(HttpStatus.CREATED).body(service.register(request));
 * }
 * }</pre>
 *
 * @author Mateus Silva
 * @since 1.0.0
 */

@Target(METHOD)
@Retention(RUNTIME)
@Operation

@DocCode201
@DocCode400
@DocCode401
@DocCode403
@DocCode409
@DocCode422
@DocCode500
public @interface ApiDocPost {
    /**
     * Custom OpenAPI operation summary.
     *
     * @return the custom operation summary
     */
    @AliasFor(annotation = Operation.class, attribute = "summary")
    String summary() default "";

    /**
     * Custom OpenAPI operation description.
     *
     * @return the custom operation description
     */
    @AliasFor(annotation = Operation.class, attribute = "description")
    String description() default "";

    /**
     * Custom global error schema class for this operation.
     *
     * <p>If set to {@code Void.class}, DocFlow automatically uses
     * the default global error schema configured in application properties.</p>
     *
     * @return the custom error schema class
     */
    Class<?> errorSchema() default Void.class;
}
