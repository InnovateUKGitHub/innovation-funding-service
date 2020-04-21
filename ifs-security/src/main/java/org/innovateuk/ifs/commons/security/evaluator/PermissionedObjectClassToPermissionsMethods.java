package org.innovateuk.ifs.commons.security.evaluator;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * An alias for a map of protected classes (e.g. ApplicationResource) against the permission methods that
 * protect them.
 */
public class PermissionedObjectClassToPermissionsMethods extends LinkedHashMap<Class<?>, ListOfOwnerAndMethod> {
}
