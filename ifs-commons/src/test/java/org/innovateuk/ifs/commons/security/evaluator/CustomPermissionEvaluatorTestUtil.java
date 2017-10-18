package org.innovateuk.ifs.commons.security.evaluator;

import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper util for testing in and around the {@link AbstractCustomPermissionEvaluator}
 */
public class CustomPermissionEvaluatorTestUtil {

    public static PermissionedObjectClassToPermissionsToPermissionsMethods getRulesMap(AbstractCustomPermissionEvaluator permissionEvaluator) {
        PermissionMethodHandler permissionMethodHandler = getPermissionMethodHandler(permissionEvaluator);
        return (PermissionedObjectClassToPermissionsToPermissionsMethods) ReflectionTestUtils.getField(permissionMethodHandler, "rulesMap");
    }

    public static PermissionMethodHandler getPermissionMethodHandler(AbstractCustomPermissionEvaluator permissionEvaluator) {
        return (PermissionMethodHandler) ReflectionTestUtils.getField(permissionEvaluator, "permissionMethodHandler");
    }

    public static Map<Class<?>, ListOfOwnerAndMethod> getPermissionLookupStrategyMap(AbstractCustomPermissionEvaluator permissionEvaluator) {
        return (Map<Class<?>, ListOfOwnerAndMethod>) ReflectionTestUtils.getField(permissionEvaluator, "lookupStrategyMap");
    }

    public static void setRulesMap(AbstractCustomPermissionEvaluator permissionEvaluator, PermissionedObjectClassToPermissionsToPermissionsMethods newValue) {
        PermissionMethodHandler permissionMethodHandler = getPermissionMethodHandler(permissionEvaluator);
        ReflectionTestUtils.setField(permissionMethodHandler, "rulesMap", newValue);
    }

    public static void cleanDownCachedPermissionRules(AbstractCustomPermissionEvaluator permissionEvaluator) {
        PermissionMethodHandler handler = getPermissionMethodHandler(permissionEvaluator);
        ReflectionTestUtils.setField(handler, "securingMethodsPerClassAndPermission", new HashMap<>());
    }
}
