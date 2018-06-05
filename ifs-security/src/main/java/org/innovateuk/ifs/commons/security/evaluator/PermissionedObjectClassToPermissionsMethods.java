package org.innovateuk.ifs.commons.security.evaluator;

import java.util.HashMap;

/**
 * An alias for a map of protected classes (e.g. ApplicationResource) against the permission methods that
 * protect them.
 */
public class PermissionedObjectClassToPermissionsMethods extends HashMap<Class<?>, ListOfOwnerAndMethod> {
}
