package org.innovateuk.ifs.commons;


import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.evaluator.ListOfOwnerAndMethod;
import org.innovateuk.ifs.commons.security.evaluator.PermissionsToPermissionsMethods;

import java.lang.reflect.Method;
import java.util.List;

import static org.apache.commons.lang3.tuple.Pair.of;
import static org.innovateuk.ifs.commons.security.evaluator.ListOfOwnerAndMethod.from;
import static org.innovateuk.ifs.util.CollectionFunctions.flattenLists;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

/**
 * Class to represent the rules for a particular secured class.
 */
public class PermissionRulesClassResult {

    Class<?> securedClass;
    List<Pair<Method, PermissionRule>> rules;

    private PermissionRulesClassResult(Class<?> securedClass, List<Pair<Method, PermissionRule>> rules){
        this.securedClass = securedClass;
        this.rules = rules;
    }

    public static PermissionRulesClassResult fromClassAndPermissionMethods(Class<?> securedClass, PermissionsToPermissionsMethods permissionsToPermissionsMethods){
        // We are reverse engineering the PermissionRule Annotations which sit on the permission rule methods
        // To do we inspect the internals of the CustomPermissionEvaluator to get at all the Methods it has aggregated
        // and which it will call when it needs to ascertain whether a user has permission to do something. These
        // methods are then inspected for the PermissionRule annotation which should sit on them.
        //
        // At this point we are only concerned with a particular secured class.
        // First we get a List of a combination of a bean instance which contain a security method and the Method
        // itself.
        ListOfOwnerAndMethod listOfOwningBeanWithSecurityMethod = from(flattenLists(permissionsToPermissionsMethods.values()));
        // We are not interested in the bean so can discard it and are then left with the Methods alone.
        List<Method> securityMethods = simpleMap(listOfOwningBeanWithSecurityMethod, Pair::getValue);
        // Now we can get the PermissionRule Annotation as well
        List<Pair<Method, PermissionRule>> rules = simpleMap(securityMethods, method -> of(method, findAnnotation(method, PermissionRule.class)));
        return new PermissionRulesClassResult(securedClass, rules);
    }
}
