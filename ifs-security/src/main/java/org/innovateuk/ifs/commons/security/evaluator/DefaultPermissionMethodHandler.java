package org.innovateuk.ifs.commons.security.evaluator;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.application.resource.FormInputResponseCommand;
import org.innovateuk.ifs.commons.exception.ForbiddenActionException;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.security.authentication.user.UserAuthentication;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

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


        // Permissions have failed, it is useful to log out some salient details. However if they end up spamming the
        // logs the level may have to be put down as denying access is not exceptional application behaviour.
        LOG.warn(detailedAccessDeniedMessage(authentication, targetObject, permission, targetClass));
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
            // We want a UserResource to feed into the @PermissonRule-method. If we don't have one we have to throw.
            finalAuthentication = from(authentication).orElseThrow(() ->
            new IllegalArgumentException("Unable to determine the authentication token for Spring Security"));
        }
        else if (Authentication.class.isAssignableFrom(secondParameter)) {
            // Well also allow Authentication objects to be feed into @PermissonRule-methods.
            finalAuthentication = authentication;
        } else {
            throw new IllegalArgumentException("Second parameter of @PermissionRule-annotated method " + method.getName() + " should be " +
                    "either an instance of " + UserResource.class.getName() + " or an org.springframework.security.core.Authentication implementation, " +
                    "but was " + secondParameter.getName());
        }

        try {
            return (Boolean) method.invoke(methodAndBean.getLeft(), dto, finalAuthentication);
        } catch (InvocationTargetException e) {
            LOG.error("Error whilst processing a permissions method", e);
            if (e.getTargetException() instanceof ObjectNotFoundException || e.getTargetException() instanceof ForbiddenActionException) {
                throw (RuntimeException) e.getTargetException();
            } else {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            LOG.error("Error whilst processing a permissions method", e);
            throw new RuntimeException(e);
        }
    }

    private Optional<UserResource> from(Authentication authentication){
        if (authentication instanceof UserAuthentication) {
            return Optional.of(((UserAuthentication) authentication).getDetails());
        } else if (authentication instanceof AnonymousAuthenticationToken) {
            return Optional.of(ANONYMOUS_USER);
        } else {
            return Optional.empty();
        }
    }

    private String detailedAccessDeniedMessage(Authentication authentication, Object targetObject, Object permission, Class<?> targetClass){
        StringBuilder message = new StringBuilder();
        message.append("Failed authentication ");
        Optional<UserResource> user = from(authentication);
        if (user.isPresent()){
            message.append("user [id:");
            message.append(ANONYMOUS_USER.equals(user) ? "anonymous" : user.get().getId());
            message.append("] ");
        }
        else {
            message.append("authentication [] ");
        }

        message.append("permission [" + (permission != null ? permission.toString() : "null") + "] ");
        message.append("targetClass [" + (targetClass != null ? targetClass.getSimpleName() : "null") + "] ");
        message.append(detailedAccessDeniedMessageTarget(targetObject));
        return message.toString();
    }

    private String detailedAccessDeniedMessageTarget(Object targetObject){

        if (targetObject == null) {
            return "target [null]";
        }
        // May need more instanceof checks to obtain more detailed information.
        if (targetObject instanceof FormInputResponseCommand) {
            FormInputResponseCommand firc = (FormInputResponseCommand)targetObject;
            return "target [userId:" + firc.getUserId() + " formInputId:" + firc.getFormInputId() + " applicationId:" + firc.getApplicationId() + "]";
        }
        else {
            Optional<Object> targetId = getId(targetObject);
             return "target [id:" + (targetId.isPresent() ? targetId.get() : "null") + "]";
        }
    }

    private Optional<Object> getId(Object dto){
        Method getId = ReflectionUtils.findMethod(dto.getClass(), "getId");
        try {
            return Optional.of(ReflectionUtils.invokeMethod(getId, dto));
        }
        catch (Exception e) {
            // Not much that we can do here and we don't want to cause issues just for logging.
            return Optional.empty();
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
