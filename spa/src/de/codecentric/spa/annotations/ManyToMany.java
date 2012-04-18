package de.codecentric.spa.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Currently not supported.
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface ManyToMany {

}
