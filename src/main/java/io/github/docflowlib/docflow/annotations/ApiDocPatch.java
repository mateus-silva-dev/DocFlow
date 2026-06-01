package io.github.docflowlib.docflow.annotations;

import io.github.docflowlib.docflow.internal.annotations.errorClient.*;
import io.github.docflowlib.docflow.internal.annotations.serverError.DocCode500;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Documents an HTTP PATCH endpoint using DocFlow automation.
 *
 * <p>DocFlow automatically generates OpenAPI documentation for the
 * annotated operation, including response schemas and standard
 * HTTP status codes for partial resource updates.</p>
 *
 * <h3>Default Responses</h3>
 * <ul>
 *   <li>200 - Success (OK)</li>
 *   <li>204 - No Content</li>
 *   <li>400 - Bad Request</li>
 *   <li>401 - Unauthorized</li>
 *   <li>403 - Forbidden</li>
 *   <li>404 - Not Found</li>
 *   <li>422 - Unprocessable Entity</li>
 *   <li>500 - Internal Server Error</li>
 * </ul>
 *
 * <p>Example usage (Automatic Mode):</p>
 * <pre>{@code
 * @PatchMapping("/{id}/status")
 * @ApiDocPatch
 * public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody StatusRequest request) {
 *     service.updateStatus(id, request);
 *     return ResponseEntity.noContent().build();
 * }
 * }</pre>
 *
 * <p>Example usage (Custom Mode - Overriding metadata):</p>
 * <pre>{@code
 * @PatchMapping("/{id}/status")
 * @ApiDocPatch(
 *     summary = "Patch user account status",
 *     description = "Partially updates user active status flag without modifying other fields"
 * )
 * public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestBody StatusRequest request) {
 *     service.updateStatus(id, request);
 *     return ResponseEntity.noContent().build();
 * }
 * }</pre>
 *
 * @author Mateus Silva
 * @since 1.0.0
 */

@Target(METHOD)
@Retention(RUNTIME)
@Operation

@DocCode400
@DocCode401
@DocCode403
@DocCode404
@DocCode422
@DocCode500
public @interface ApiDocPatch {
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

    /**
     * Determines whether the response should include a representation of the resource body.
     *
     * <p>If {@code true}, DocFlow will document the operation response schema.
     * If {@code false}, the response will be documented without a body payload.</p>
     *
     * @return {@code true} if the response includes a body, {@code false} otherwise
     */
    boolean withBody() default false;
}
