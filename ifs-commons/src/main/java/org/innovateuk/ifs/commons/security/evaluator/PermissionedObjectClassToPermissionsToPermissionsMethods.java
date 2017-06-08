package org.innovateuk.ifs.commons.security.evaluator;

import java.util.HashMap;

/**
 * An alias for a map that maps protected classes (e.g. ApplicationResource) to the permissions methods that
 * protect them.  Specifically, it maps them to a {@link PermissionsToPermissionsMethods} which holds
 * a list of protected actions (e.g. "READ", "WRITE") and the permission methods that support each action.
 * Therefore this map holds the means by which to look up, for example, all permission methods that
 * protect an ApplicationResource against a "READ" action.
 */
public class PermissionedObjectClassToPermissionsToPermissionsMethods
        extends HashMap<Class<?>, PermissionsToPermissionsMethods> {
}
