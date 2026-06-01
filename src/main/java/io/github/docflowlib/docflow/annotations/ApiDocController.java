package io.github.docflowlib.docflow.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks a controller to be processed by DocFlow.
 *
 * <p>When applied to a Spring MVC controller, DocFlow automatically
 * generates and organizes OpenAPI tags based on the provided metadata.</p>
 *
 * <p>If no values are specified, DocFlow will switch to <strong>Automatic Mode</strong>,
 * generating sensible defaults using the controller class name.</p>
 *
 * <p>Example usage (Automatic Mode - No parameters):</p>
 * <pre>{@code
 * @RestController
 * @RequestMapping("/orders")
 * @ApiDocController
 * public class OrderController {
 * }
 * }</pre>
 *
 * <p>Example usage (Custom Mode - With parameters):</p>
 * <pre>{@code
 * @RestController
 * @RequestMapping("/users")
 * @ApiDocController(
 *     tagName = "Users",
 *     tagDescription = "User management endpoints"
 * )
 * public class UserController {
 * }
 * }</pre>
 *
 * @author Mateus Silva
 * @since 1.0.0
 */

@Target(TYPE)
@Retention(RUNTIME)
public @interface ApiDocController {

    /**
     * Custom OpenAPI tag name.
     *
     * <p>If empty, DocFlow automatically generates
     * the tag name from the controller class name.</p>
     *
     * @return the custom tag name
     */
    String tagName() default "";

    /**
     * Custom OpenAPI tag description.
     *
     * <p>If empty, DocFlow automatically generates
     * a default tag description.</p>
     *
     * @return the custom tag description
     */
    String tagDescription() default "";
}
