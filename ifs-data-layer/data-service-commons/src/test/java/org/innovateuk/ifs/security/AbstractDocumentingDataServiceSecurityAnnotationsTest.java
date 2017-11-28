package org.innovateuk.ifs.security;

import org.innovateuk.ifs.commons.AbstractDocumentingServiceSecurityAnnotationsTest;
import org.innovateuk.ifs.commons.security.evaluator.RootCustomPermissionEvaluator;
import org.innovateuk.ifs.security.evaluator.CustomPermissionEvaluator;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Base class to document security permissions and rules in the data layer
 */
public abstract class AbstractDocumentingDataServiceSecurityAnnotationsTest extends AbstractDocumentingServiceSecurityAnnotationsTest {

    @Override
    protected final RootCustomPermissionEvaluator evaluator() {
        return (CustomPermissionEvaluator) context.getBean("customPermissionEvaluator");

    }

    @Override
    protected final List<Class<?>> excludedClasses() {
        List<Class<?>> union = new ArrayList<>();
        union.addAll(asList(UidAuthenticationService.class, StatelessAuthenticationFilter.class));
        union.addAll(additionalExcludedClasses());
        return union;
    }

    /**
     * Add classes which are not present in this base project but still need to be ignored.
     * @return
     */
    protected abstract List<Class<?>> additionalExcludedClasses();

}
