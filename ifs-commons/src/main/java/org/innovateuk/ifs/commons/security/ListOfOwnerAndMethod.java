package org.innovateuk.ifs.commons.security;

import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * An Alias for a List of owning Objects and a single Method on the owning Object.
 * Thus representing a List of callable functions.
 */
public class ListOfOwnerAndMethod extends ArrayList<Pair<Object, Method>> {

    public static ListOfOwnerAndMethod from(List<Pair<Object, Method>> list) {
        ListOfOwnerAndMethod listOfMethods = new ListOfOwnerAndMethod();
        listOfMethods.addAll(list);
        return listOfMethods;
    }
}
