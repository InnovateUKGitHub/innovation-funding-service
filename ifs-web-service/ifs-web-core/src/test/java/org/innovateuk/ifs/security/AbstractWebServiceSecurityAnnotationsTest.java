package org.innovateuk.ifs.security;

import org.innovateuk.ifs.async.controller.AwaitAsyncFuturesCompletionIntegrationTestHelper;
import org.innovateuk.ifs.async.controller.ThreadsafeModelAopIntegrationTestHelper;
import org.innovateuk.ifs.commons.AbstractServiceSecurityAnnotationsTest;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.security.evaluator.RootCustomPermissionEvaluator;
import org.innovateuk.ifs.exception.IfsErrorController;
import org.innovateuk.ifs.security.evaluator.CustomPermissionEvaluator;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.stereotype.Controller;

import java.lang.annotation.Annotation;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.util.CollectionFunctions.union;

/**
 * Base class in the web core to ensure that all controllers are secured as appropriate.
 */
public abstract class AbstractWebServiceSecurityAnnotationsTest extends AbstractServiceSecurityAnnotationsTest {

    // classes to exclude from testing
    public static final List<Class<?>> GLOBAL_EXCLUDED_CLASSES = asList(
            IfsErrorController.class,
            ThreadsafeModelAopIntegrationTestHelper.class,
            AwaitAsyncFuturesCompletionIntegrationTestHelper.class);

    @Override
    protected final List<Class<? extends Annotation>> classLevelSecurityAnnotations() {
        return  asList(
                PreAuthorize.class,
                PostAuthorize.class
        );
    }

    // We currently do security at the controller level.
    @Override
    protected final List<Class<? extends Annotation>> annotationsOnClassesToSecure() {
        return asList(Controller.class);
    }

    @Override
    protected final List<Class<? extends Annotation>> methodLevelSecurityAnnotations() {
        return asList(
                PreAuthorize.class,
                PreFilter.class,
                PostAuthorize.class,
                PostFilter.class,
                NotSecured.class
        );
    }

    @Override
    protected final RootCustomPermissionEvaluator evaluator() {
        return (CustomPermissionEvaluator) context.getBean("customPermissionEvaluator");
    }

    @Override
    protected final List<Class<?>> excludedClasses() {
        return union(GLOBAL_EXCLUDED_CLASSES, additionalExcludedClasses());
    }

    /**
     * Add classes which are not present in this base project but still need to be ignored.
     * @return
     */
    protected abstract List<Class<?>> additionalExcludedClasses();
}
