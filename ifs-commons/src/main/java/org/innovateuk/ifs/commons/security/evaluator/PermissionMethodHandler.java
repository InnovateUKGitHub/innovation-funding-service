package org.innovateuk.ifs.commons.security.evaluator;

import org.springframework.security.core.Authentication;

import java.util.List;

/**
 * This class is responsible for performing permission checks, given a target object (e.g. ApplicationResource) and an
 * action key (e.g. "READ")
 */
public interface PermissionMethodHandler {

    boolean hasPermission(Authentication authentication, Object targetObject, Object permission, Class<?> targetClass);

    List<String> getPermissions(Authentication authentication, Object targetDomainObject);
}
