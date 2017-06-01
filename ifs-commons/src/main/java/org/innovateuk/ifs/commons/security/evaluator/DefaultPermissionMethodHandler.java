package org.innovateuk.ifs.commons.security.evaluator;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.security.authentication.user.UserAuthentication;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;

/**
 * Given a set of permission methods, this class will perform permission checks given an object (e.g. an ApplicationResource)
 * and a permission action (e.g. "READ", "WRITE").  In addition, this class caches sets of permission methods against protected
 * object classes and permission actions.
 */
public class DefaultPermissionMethodHandler implements PermissionMethodHandler {

    private static final Log LOG = LogFactory.getLog(PermissionMethodHandler.class);

    private static final UserResource ANONYMOUS_USER = new UserResource();

    private PermissionedObjectClassToPermissionsToPermissionsMethods rulesMap;

    private Map<Pair<Class<?>, Object>, ListOfOwnerAndMethod> securingMethodsPerClassAndPermission = new HashMap<>();

    public DefaultPermissionMethodHandler(PermissionedObjectClassToPermissionsToPermissionsMethods rulesMap) {
        this.rulesMap = rulesMap;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetObject, Object permission, Class<?> targetClass) {

        ListOfOwnerAndMethod securingMethods = lookupAndCacheSecuringMethodsForClassAndPermission(permission, targetClass);

        for (Pair<Object, Method> methodAndBean : securingMethods) {

            if (callHasPermissionMethod(methodAndBean, targetObject, authentication)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public List<String> getPermissions(Authentication authentication, Object targetDomainObject) {
        return rulesMap.getOrDefault(targetDomainObject.getClass(), emptyPermissions()).keySet().stream().filter(
                permission -> hasPermission(authentication, targetDomainObject, permission, targetDomainObject.getClass())
        ).sorted().collect(toList());
    }

    private ListOfOwnerAndMethod lookupAndCacheSecuringMethodsForClassAndPermission(Object permission, Class<?> targetClass) {

        Pair<Class<?>, Object> classAndPermissionPair = Pair.of(targetClass, permission);
        ListOfOwnerAndMethod cachedPermissions = securingMethodsPerClassAndPermission.get(classAndPermissionPair);

        if (cachedPermissions != null) {
            return cachedPermissions;
        }

        ListOfOwnerAndMethod permissionMethodsForPermissionAggregate = findSecuringMethodsPerClass(permission, targetClass);
        securingMethodsPerClassAndPermission.put(classAndPermissionPair, permissionMethodsForPermissionAggregate);
        return permissionMethodsForPermissionAggregate;
    }

    private ListOfOwnerAndMethod findSecuringMethodsPerClass(Object permission, Class<?> targetClass) {

        final List<PermissionsToPermissionsMethods> permissionsWithPermissionsMethodsForTargetClassList
                = rulesMap.entrySet().stream().
                filter(e -> e.getKey().isAssignableFrom(targetClass)). // Any super class of the target class will do.
                map(Map.Entry::getValue).collect(toList());

        final List<ListOfOwnerAndMethod> permissionMethodsForPermissionList
                = permissionsWithPermissionsMethodsForTargetClassList.stream().
                map(permissionsToPermissionsMethods -> permissionsToPermissionsMethods.get(permission)).
                filter(Objects::nonNull). // Filter any nulls
                collect(toList());

        return permissionMethodsForPermissionList.stream().
                reduce(new ListOfOwnerAndMethod(), (f1, f2) -> ListOfOwnerAndMethod.from(combineLists(f1, f2)));
    }

    private boolean callHasPermissionMethod(Pair<Object, Method> methodAndBean, Object dto, Authentication authentication) {

        final Object finalAuthentication;

        Method method = methodAndBean.getValue();
        Class<?> secondParameter = method.getParameterTypes()[1];

        if (secondParameter.equals(UserResource.class)) {
            if (authentication instanceof UserAuthentication) {
                finalAuthentication = ((UserAuthentication) authentication).getDetails();
            } else if (authentication instanceof AnonymousAuthenticationToken) {
                finalAuthentication = ANONYMOUS_USER;
            } else {
                throw new IllegalArgumentException("Unable to determine the authentication token for Spring Security");
            }
        } else if (Authentication.class.isAssignableFrom(secondParameter)) {
            finalAuthentication = authentication;
        } else {
            throw new IllegalArgumentException("Second parameter of @PermissionRule-annotated method " + method.getName() + " should be " +
                    "either an instance of " + UserResource.class.getName() + " or an org.springframework.security.core.Authentication implementation, " +
                    "but was " + secondParameter.getName());
        }

        try {
            return (Boolean) method.invoke(methodAndBean.getLeft(), dto, finalAuthentication);
        } catch (Exception e) {
            LOG.error("Error whilst processing a permissions method", e);
            throw new RuntimeException(e);
        }
    }

    private static PermissionsToPermissionsMethods emptyPermissions() {
        return new PermissionsToPermissionsMethods();
    }

    public static boolean isAnonymous(UserResource user) {
        return user == ANONYMOUS_USER;
    }

    public static UserResource getAnonymous() {
        return ANONYMOUS_USER;
    }
}
