package de.codecentric.spa.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static de.codecentric.spa.annotations.FetchType.LAZY;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface ManyToOne {
	
	CascadeType[] cascade() default {};

    FetchType fetch() default LAZY;


}
