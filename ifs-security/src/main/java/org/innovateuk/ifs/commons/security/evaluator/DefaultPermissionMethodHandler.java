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

    private boolean callHasPermissionMethod(Pair<Object, Method> beanAndMethod, Object dto, Authentication authentication) {

        Method method = beanAndMethod.getValue();
        Class<?> secondParameter = method.getParameterTypes()[1];

        if (secondParameter.equals(UserResource.class)) {
            // We want a UserResource to feed into the @PermissonRule-method. If we don't have one we have to throw.
            UserResource currentUser = from(authentication).orElseThrow(() ->
            new IllegalArgumentException("Unable to determine the authentication token for Spring Security"));
            return invokePermissionMethod(beanAndMethod, dto, currentUser);
        }
        else if (Authentication.class.isAssignableFrom(secondParameter)) {
            // Well also allow Authentication objects to be feed into @PermissonRule-methods.
            return invokePermissionMethod(beanAndMethod, dto, authentication);
        } else {
            throw new IllegalArgumentException("Second parameter of @PermissionRule-annotated method " + method.getName() + " should be " +
                    "either an instance of " + UserResource.class.getName() + " or an org.springframework.security.core.Authentication implementation, " +
                    "but was " + secondParameter.getName());
        }
    }

    private boolean invokePermissionMethod(Pair<Object, Method> methodAndBean, Object dto, Object authentication) {
        try {
            return (Boolean) methodAndBean.getRight().invoke(methodAndBean.getLeft(), dto, authentication);
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
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Failed authentication ");
        Optional<UserResource> user = from(authentication);
        if (user.isPresent()){
            messageBuilder.append("user [id:");
            messageBuilder.append(ANONYMOUS_USER.equals(user.get()) ? "anonymous" : user.get().getId());
            messageBuilder.append("] ");
        }
        else {
            messageBuilder.append("authentication [] ");
        }

        messageBuilder.append("permission [" + (permission != null ? permission.toString() : "null") + "] ");
        messageBuilder.append("targetClass [" + (targetClass != null ? targetClass.getSimpleName() : "null") + "] ");
        messageBuilder.append(detailedAccessDeniedMessageTarget(targetObject));
        return messageBuilder.toString();
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
