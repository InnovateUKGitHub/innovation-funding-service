package org.innovateuk.ifs.commons.security.evaluator;

import java.util.HashMap;

/**
 * An alias for a permission action (e.g. "READ", "WRITE") and a list of permission methods that
 * protect that particular action.
 *
 * A protected type (e.g. ApplicationResource) will likely have multiple instances of these, one for
 * each action that they are protected against.
 */
public class PermissionsToPermissionsMethods extends HashMap<String, ListOfOwnerAndMethod> {
}
