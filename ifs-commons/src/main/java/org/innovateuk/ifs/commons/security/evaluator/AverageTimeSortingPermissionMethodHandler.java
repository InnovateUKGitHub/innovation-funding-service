package org.innovateuk.ifs.commons.security.evaluator;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.security.authentication.user.UserAuthentication;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.util.CollectionFunctions.combineLists;

/**
 * Give a set of permission methods, this class will perform permission checks given an object (e.g. an ApplicationResource)
 * and a permission action (e.g. "READ", "WRITE").  In addition, this class performs sampling of the duration of different
 * permission methods for a given class and action, and will order them to prefer the quickest performing checks first.
 */
public class AverageTimeSortingPermissionMethodHandler implements PermissionMethodHandler {

    private static final Log LOG = LogFactory.getLog(PermissionMethodHandler.class);

    private static final UserResource ANONYMOUS_USER = new UserResource();

    private static final int MAX_SAMPLES_TO_AVERAGE_OVER = 10000;

    private PermissionedObjectClassToPermissionsToPermissionsMethods rulesMap;

    private Map<Pair<Class<?>, Object>, ListOfOwnerAndMethod> securingMethodsPerClassAndPermission = new HashMap<>();

    private Map<Pair<Object, Method>, Pair<Long, Long>> averageResponseTimesPerPermissionCheck = new HashMap<>();

    private Sampler sampler = new Sampler();

    public AverageTimeSortingPermissionMethodHandler(PermissionedObjectClassToPermissionsToPermissionsMethods rulesMap) {
        this.rulesMap = rulesMap;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetObject, Object permission, Class<?> targetClass) {

        ListOfOwnerAndMethod securingMethods = getSecuringMethodsForClassAndPermission(permission, targetClass);

        for (Pair<Object, Method> methodAndBean : securingMethods) {

            if (hasPermissionWithTimingUpdate(authentication, targetObject, permission, targetClass, securingMethods, methodAndBean)) {
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

    private ListOfOwnerAndMethod getSecuringMethodsForClassAndPermission(Object permission, Class<?> targetClass) {

        Pair<Class<?>, Object> classAndPermissionPair = Pair.of(targetClass, permission);
        ListOfOwnerAndMethod cachedPermissions = securingMethodsPerClassAndPermission.get(classAndPermissionPair);

        if (cachedPermissions != null) {
            return ListOfOwnerAndMethod.from(cachedPermissions);
        }

        ListOfOwnerAndMethod permissionMethodsForPermissionAggregate = findSecuringMethodsPerClass(permission, targetClass);
        securingMethodsPerClassAndPermission.put(classAndPermissionPair, permissionMethodsForPermissionAggregate);
        return ListOfOwnerAndMethod.from(permissionMethodsForPermissionAggregate);
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

    private void updateAverageAndCount(Pair<Object, Method> methodAndBean, Long time) {

        Pair<Long, Long> averageAndCount = averageResponseTimesPerPermissionCheck.get(methodAndBean);

        if (averageAndCount == null) {
            averageAndCount = Pair.of(0L, 0L);
        }

        Long currentAverage = averageAndCount.getLeft();
        Long currentCount = averageAndCount.getRight();
        long newCount = currentCount + 1;
        long totalSoFar = (currentAverage * currentCount) + time;

        final Pair<Long, Long> newAverageAndCount;

        if (totalSoFar >= 0 && newCount < MAX_SAMPLES_TO_AVERAGE_OVER) {
            long newAverage = totalSoFar / newCount;
            newAverageAndCount = Pair.of(newAverage, newCount);
        } else {
            newAverageAndCount = Pair.of(currentAverage, 1L);
        }
        averageResponseTimesPerPermissionCheck.put(methodAndBean, newAverageAndCount);
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

    private ListOfOwnerAndMethod sortSecuringMethodsByAverageTime(ListOfOwnerAndMethod unsorted) {

        List<Pair<Object, Method>> toSort = new ArrayList<>(unsorted);
        toSort.sort((o1, o2) -> {

            Pair<Long, Long> o1Average = averageResponseTimesPerPermissionCheck.get(o1);
            Pair<Long, Long> o2Average = averageResponseTimesPerPermissionCheck.get(o2);

            if (o1Average == null && o2Average == null) {
                return 0;
            }

            if (o1Average == null) {
                return -1;
            }

            if (o2Average == null) {
                return 1;
            }

            return o1Average.getLeft().compareTo(o2Average.getLeft());
        });
        return ListOfOwnerAndMethod.from(toSort);
    }

    private boolean hasPermissionWithTimingUpdate(Authentication authentication, Object targetObject, Object permission, Class<?> targetClass, ListOfOwnerAndMethod securingMethods, Pair<Object, Method> methodAndBean) {

        if (sampler.isTakeSample()) {

            Pair<Boolean, Long> hasPermissionWithTiming = sampler.hasPermissionWithTiming(() -> callHasPermissionMethod(methodAndBean, targetObject, authentication));

            Boolean hasPermission = hasPermissionWithTiming.getLeft();
            Long time = hasPermissionWithTiming.getRight();

            updateAverageAndCount(methodAndBean, time);
            ListOfOwnerAndMethod sortedMethods = sortSecuringMethodsByAverageTime(securingMethods);
            securingMethodsPerClassAndPermission.put(Pair.of(targetClass, permission), sortedMethods);

            return hasPermission;

        } else {

            return callHasPermissionMethod(methodAndBean, targetObject, authentication);
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

    /**
     * A class that is responsible for capturing samples for average lengths of different permission calls
     */
    static class Sampler {

        private static final double SAMPLING_RATE = 0.05;

        Pair<Boolean, Long> hasPermissionWithTiming(Supplier<Boolean> hasPermissionCall) {

            long before = System.currentTimeMillis();
            boolean hasPermission = hasPermissionCall.get();
            long after = System.currentTimeMillis();
            long time = after - before;

            return Pair.of(hasPermission, time);
        }

        boolean isTakeSample() {
            return Math.random() < SAMPLING_RATE;
        }
    }
}
