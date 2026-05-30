package io.github.docflowlib.docflow.annotations;

import io.github.docflowlib.docflow.internal.annotations.errorClient.DocCode401;
import io.github.docflowlib.docflow.internal.annotations.errorClient.DocCode403;
import io.github.docflowlib.docflow.internal.annotations.errorClient.DocCode404;
import io.github.docflowlib.docflow.internal.annotations.serverError.DocCode500;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(METHOD)
@Retention(RUNTIME)
@Operation

@DocCode401
@DocCode403
@DocCode404
@DocCode500
public @interface ApiDocDelete {
    @AliasFor(annotation = Operation.class, attribute = "summary")
    String summary() default "";

    @AliasFor(annotation = Operation.class, attribute = "description")
    String description() default "";

    Class<?> errorSchema() default Void.class;

    boolean withBody() default false;
}
