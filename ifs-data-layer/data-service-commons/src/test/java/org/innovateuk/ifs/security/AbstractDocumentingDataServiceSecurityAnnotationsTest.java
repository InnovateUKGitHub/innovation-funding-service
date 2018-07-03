package org.innovateuk.ifs.security;

import org.innovateuk.ifs.commons.AbstractDocumentingServiceSecurityAnnotationsTest;
import org.innovateuk.ifs.commons.security.evaluator.RootCustomPermissionEvaluator;
import org.innovateuk.ifs.security.evaluator.CustomPermissionEvaluator;
import org.springframework.stereotype.Service;

import java.lang.annotation.Annotation;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.util.CollectionFunctions.union;

/**
 * Base class to document security permissions and rules in the data layer
 */
public abstract class AbstractDocumentingDataServiceSecurityAnnotationsTest extends AbstractDocumentingServiceSecurityAnnotationsTest {

    @Override
    protected List<Class<? extends Annotation>> annotationsOnClassesToSecure() {
        return singletonList(Service.class);
    }


    @Override
    protected final RootCustomPermissionEvaluator evaluator() {
        return (CustomPermissionEvaluator) context.getBean("customPermissionEvaluator");

    }

    @Override
    protected final List<Class<?>> excludedClasses() {
        return union(asList(UidAuthenticationService.class, StatelessAuthenticationFilter.class), additionalExcludedClasses());
    }

    /**
     * Add classes which are not present in this base project but still need to be ignored.
     * @return
     */
    protected abstract List<Class<?>> additionalExcludedClasses();


}
