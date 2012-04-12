package de.codecentric.spa.annotations;

import static de.codecentric.spa.annotations.FetchType.LAZY;
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

	CascadeType[] cascade() default {};

	FetchType fetch() default LAZY;

}
