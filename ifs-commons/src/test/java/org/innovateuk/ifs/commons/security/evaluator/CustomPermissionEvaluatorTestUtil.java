package org.innovateuk.ifs.commons.security.evaluator;

import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

/**
 * TODO DW - add comment
 */
public class CustomPermissionEvaluatorTestUtil {

    public static PermissionedObjectClassToPermissionsToPermissionsMethods getRulesMap(CustomPermissionEvaluator permissionEvaluator) {
        PermissionMethodHandler permissionMethodHandler = getPermissionMethodHandler(permissionEvaluator);
        return (PermissionedObjectClassToPermissionsToPermissionsMethods) ReflectionTestUtils.getField(permissionMethodHandler, "rulesMap");
    }

    public static PermissionMethodHandler getPermissionMethodHandler(CustomPermissionEvaluator permissionEvaluator) {
        return (PermissionMethodHandler) ReflectionTestUtils.getField(permissionEvaluator, "permissionMethodSupplier");
    }

    public static Map<Class<?>, ListOfOwnerAndMethod> getPermissionLookupStrategyMap(CustomPermissionEvaluator permissionEvaluator) {
        return (Map<Class<?>, ListOfOwnerAndMethod>) ReflectionTestUtils.getField(permissionEvaluator, "lookupStrategyMap");
    }

    public static void setRuleMap(CustomPermissionEvaluator permissionEvaluator, PermissionedObjectClassToPermissionsToPermissionsMethods newValue) {
        PermissionMethodHandler permissionMethodHandler = getPermissionMethodHandler(permissionEvaluator);
        ReflectionTestUtils.setField(permissionMethodHandler, "rulesMap", newValue);
    }
}
