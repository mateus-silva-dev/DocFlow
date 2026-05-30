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
    @AliasFor(annotation = Operation.class, attribute = "summary")
    String summary() default "";

    @AliasFor(annotation = Operation.class, attribute = "description")
    String description() default "";

    Class<?> errorSchema() default Void.class;
}
