package org.innovateuk.ifs.commons.security;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO DW - document this class
 */
public class PermissionedObjectClassesToListOfLookup extends HashMap<Class<?>, ListOfOwnerAndMethod> {

    public static PermissionedObjectClassesToListOfLookup from(Map<Class<?>, ListOfOwnerAndMethod> map) {
        final PermissionedObjectClassesToListOfLookup dtoClassToLookupMethod = new PermissionedObjectClassesToListOfLookup();
        dtoClassToLookupMethod.putAll(map);
        return dtoClassToLookupMethod;
    }
}
