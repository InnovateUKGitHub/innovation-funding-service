package org.innovateuk.ifs.commons.security.evaluator;

import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper util for testing in and around the {@link CustomPermissionEvaluator}
 */
public class CustomPermissionEvaluatorTestUtil {

    public static PermissionedObjectClassToPermissionsToPermissionsMethods getRulesMap(CustomPermissionEvaluator permissionEvaluator) {
        PermissionMethodHandler permissionMethodHandler = getPermissionMethodHandler(permissionEvaluator);
        return (PermissionedObjectClassToPermissionsToPermissionsMethods) ReflectionTestUtils.getField(permissionMethodHandler, "rulesMap");
    }

    public static PermissionMethodHandler getPermissionMethodHandler(CustomPermissionEvaluator permissionEvaluator) {
        return (PermissionMethodHandler) ReflectionTestUtils.getField(permissionEvaluator, "permissionMethodHandler");
    }

    public static Map<Class<?>, ListOfOwnerAndMethod> getPermissionLookupStrategyMap(CustomPermissionEvaluator permissionEvaluator) {
        return (Map<Class<?>, ListOfOwnerAndMethod>) ReflectionTestUtils.getField(permissionEvaluator, "lookupStrategyMap");
    }

    public static void setRulesMap(CustomPermissionEvaluator permissionEvaluator, PermissionedObjectClassToPermissionsToPermissionsMethods newValue) {
        PermissionMethodHandler permissionMethodHandler = getPermissionMethodHandler(permissionEvaluator);
        ReflectionTestUtils.setField(permissionMethodHandler, "rulesMap", newValue);
    }

    public static void cleanDownCachedPermissionRules(CustomPermissionEvaluator permissionEvaluator) {
        PermissionMethodHandler handler = getPermissionMethodHandler(permissionEvaluator);
        ReflectionTestUtils.setField(handler, "securingMethodsPerClassAndPermission", new HashMap<>());
    }
}
