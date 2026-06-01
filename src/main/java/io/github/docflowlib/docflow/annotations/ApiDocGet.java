package io.github.docflowlib.docflow.annotations;

import io.github.docflowlib.docflow.internal.annotations.errorClient.DocCode401;
import io.github.docflowlib.docflow.internal.annotations.errorClient.DocCode403;
import io.github.docflowlib.docflow.internal.annotations.errorClient.DocCode404;
import io.github.docflowlib.docflow.internal.annotations.serverError.DocCode500;
import io.github.docflowlib.docflow.internal.annotations.success.DocCode200;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Documents an HTTP GET endpoint.
 *
 * <p>DocFlow automatically generates OpenAPI documentation for the
 * annotated operation, including response schemas and standard
 * HTTP status codes.</p>
 *
 * <h3>Default Responses</h3>
 * <ul>
 *   <li>200 - Success (OK)</li>
 *   <li>401 - Unauthorized</li>
 *   <li>403 - Forbidden</li>
 *   <li>404 - Not Found</li>
 *   <li>500 - Internal Server Error</li>
 * </ul>
 *
 * <p>Example usage (Automatic Mode):</p>
 * <pre>{@code
 * @GetMapping("/{id}")
 * @ApiDocGet
 * public ResponseEntity<UserResponse> findById(UUID id) {
 *     return ResponseEntity.ok(service.findById(id));
 * }
 * }</pre>
 *
 * <p>Example usage (Custom Mode - Overriding metadata):</p>
 * <pre>{@code
 * @GetMapping("/search")
 * @ApiDocGet(
 *     summary = "Search users by filter",
 *     description = "Retrieves a paginated list of active users matching the criteria"
 * )
 * public ResponseEntity<Page<UserResponse>> search(UserFilter filter) {
 *     return ResponseEntity.ok(service.search(filter));
 * }
 * }</pre>
 *
 * @author Mateus Silva
 * @since 1.0.0
 */

@Target(METHOD)
@Retention(RUNTIME)
@Operation

@DocCode200
@DocCode401
@DocCode403
@DocCode404
@DocCode500
public @interface ApiDocGet {
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
