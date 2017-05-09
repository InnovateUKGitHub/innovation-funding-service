package org.innovateuk.ifs.commons.security.evaluator;

import java.util.HashMap;
import java.util.Map;

/**
 * An alias for a map that contains a list of protected classes (e.g. ApplicationResource) and the
 * lookup mechanisms that are available to look them up.  An example would be a method that is able to
 * look up an ApplicationResource by a Long id (supported via the
 * {@link org.innovateuk.ifs.commons.security.PermissionEntityLookupStrategy} annotation)
 */
public class PermissionedObjectClassToLookupMethods extends HashMap<Class<?>, ListOfOwnerAndMethod> {

    public static PermissionedObjectClassToLookupMethods from(Map<Class<?>, ListOfOwnerAndMethod> map) {
        final PermissionedObjectClassToLookupMethods protectedClassToLookupMethod = new PermissionedObjectClassToLookupMethods();
        protectedClassToLookupMethod.putAll(map);
        return protectedClassToLookupMethod;
    }
}
