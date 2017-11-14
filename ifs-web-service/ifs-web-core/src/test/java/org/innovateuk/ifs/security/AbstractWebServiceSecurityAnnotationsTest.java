package org.innovateuk.ifs.security;

import org.innovateuk.ifs.commons.AbstractServiceSecurityAnnotationsTest;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.exception.IfsErrorController;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.stereotype.Controller;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Base class in the web core to ensure that all controllers are secured as appropriate.
 */
public abstract class AbstractWebServiceSecurityAnnotationsTest extends AbstractServiceSecurityAnnotationsTest {

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
