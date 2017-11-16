package org.innovateuk.ifs.security;

import org.innovateuk.ifs.commons.AbstractDocumentingServiceSecurityAnnotationsTest;
import org.innovateuk.ifs.commons.security.evaluator.RootCustomPermissionEvaluator;
import org.innovateuk.ifs.exception.IfsErrorController;
import org.innovateuk.ifs.security.evaluator.CustomPermissionEvaluator;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

/**
 * Base class to document security permissions and rules in the web layer
 */
public abstract class AbstractDocumentingWebServiceSecurityAnnotationsTest extends AbstractDocumentingServiceSecurityAnnotationsTest {

    @Override
    protected List<Class<? extends Annotation>> annotationsOnClassesToSecure() {
        return singletonList(Controller.class);
    }


    @Override
    protected final RootCustomPermissionEvaluator evaluator() {
        return (CustomPermissionEvaluator) context.getBean("customPermissionEvaluator");

    }

    @Override
    protected final List<Class<?>> excludedClasses() {
        List<Class<?>> union = new ArrayList<>();
        union.addAll(asList(IfsErrorController.class));
        union.addAll(additionalExcludedClasses());
        return union;
    }

    /**
     * Add classes which are not present in this base project but still need to be ignored.
     * @return
     */
    protected abstract List<Class<?>> additionalExcludedClasses();


}
