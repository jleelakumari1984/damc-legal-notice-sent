package com.notices.api.config.security;
 

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.notices.api.annotation.RequiresAccess;
import com.notices.domain.dao.user.LoginUserDao;
import com.notices.domain.enums.UserAccessLevelEnum;

import java.lang.reflect.Method;

/**
 * AOP aspect that enforces {@link RequiresAccess} on annotated methods and
 * types.
 *
 * <p>
 * Resolution order: method annotation → class annotation.
 * Access is allowed when {@code user.accessLevel <= annotation.level()}.
 */
@Slf4j
@Aspect
@Component
public class AccessCheckAspect {

    /**
     * Intercepts any bean method inside a class annotated with
     * {@code @RequiresAccess}
     * OR any method directly annotated with {@code @RequiresAccess}.
     */
    @Before("@within(com.notices.api.annotation.RequiresAccess) " +
            "|| @annotation(com.notices.api.annotation.RequiresAccess)")
    public void checkAccess(JoinPoint joinPoint) {
        RequiresAccess annotation = resolveAnnotation(joinPoint);
        if (annotation == null)
            return;

        LoginUserDao user = currentUser();

        if (user == null) {
            throw new AccessDeniedException("Authentication required");
        }

        long userLevel = user.getAccessLevel() == null ? Long.MAX_VALUE : user.getAccessLevel();
        UserAccessLevelEnum requiredLevel = annotation.level();

        if (userLevel > requiredLevel.getLevel()
                && !(user.getCanSwitchSession() != null && user.getCanSwitchSession())) {
            log.warn("Access denied for user '{}' (level={}) - required level <= {} and switch session not allowed",
                    user.getLoginName(), userLevel, requiredLevel.getLevel());
            throw new AccessDeniedException(
                    "Access denied: your access level (%d) does not meet the required level (%d)"
                            .formatted(userLevel, requiredLevel.getLevel()));
        }

        log.debug("Access granted for user '{}' (level={}) - required level <= {} or switch session allowed",
                user.getLoginName(), userLevel, requiredLevel.getLevel());
    }

    /**
     * Resolves the effective {@link RequiresAccess} annotation.
     * Method-level annotation takes precedence over class-level.
     */
    private RequiresAccess resolveAnnotation(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        RequiresAccess methodAnnotation = method.getAnnotation(RequiresAccess.class);
        if (methodAnnotation != null) {
            return methodAnnotation;
        }

        return joinPoint.getTarget().getClass().getAnnotation(RequiresAccess.class);
    }

    private LoginUserDao currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof LoginUserDao loginUserDao) {
            return loginUserDao;
        }
        return null;
    }
}
