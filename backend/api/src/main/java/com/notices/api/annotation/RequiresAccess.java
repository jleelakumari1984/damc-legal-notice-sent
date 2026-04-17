package com.notices.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.notices.domain.enums.UserAccessLevelEnum;

/**
 * Restricts access to the annotated method or class based on the authenticated
 * user's {@code accessLevel}.
 *
 * <p>
 * Access is granted when the user's {@code accessLevel <= level}.
 * Lower numbers represent higher privilege (e.g. 1 = admin, 2 = manager,
 * 3 = standard user).
 *
 * <p>
 * When placed on a class, acts as a default for all methods in that class.
 * A method-level annotation always takes precedence over a class-level one.
 *
 * <pre>
 * // admin-only endpoint
 * {@literal @}RequiresAccess(level = 1)
 * public ResponseEntity<?> adminEndpoint() { ... }
 *
 * // admin + manager
 * {@literal @}RequiresAccess(level = 2)
 * public ResponseEntity<?> managerEndpoint() { ... }
 * </pre>
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresAccess {

    /**
     * Maximum permitted access level (inclusive).
     * Users with {@code accessLevel <= level} are allowed.
     */
    UserAccessLevelEnum level();
}
