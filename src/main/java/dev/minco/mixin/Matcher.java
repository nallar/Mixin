package dev.minco.mixin;

import java.lang.annotation.*;

import dev.minco.javatransformer.api.code.IntermediateValue;

/**
 * Indicates that this method is used to provide a code fragment to match against code fragments in another method to find an injection target
 */
@java.lang.annotation.Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Matcher {
	/**
	 * Whether to require matching constant values in the input types of the fragments
	 *
	 * @see IntermediateValue#constantValue
	 */
	boolean matchConstantInputs() default false;

	/**
	 * The name of this matcher. Multiple matchers can have the same name.
	 * <p>
	 * Defaults to the name of the method.
	 *
	 * @see Inject#match()
	 */
	String name() default "";
}
