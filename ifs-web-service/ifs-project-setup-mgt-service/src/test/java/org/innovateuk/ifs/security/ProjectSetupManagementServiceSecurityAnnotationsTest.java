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
import java.util.List;

import static java.util.Arrays.asList;

public class ProjectSetupManagementServiceSecurityAnnotationsTest extends AbstractServiceSecurityAnnotationsTest {

    @Override
    protected List<Class<? extends Annotation>> classLevelSecurityAnnotations() {
        return  asList(
                PreAuthorize.class,
                PostAuthorize.class
        );
    }

    // We currently do security at the controller level.
    @Override
    protected List<Class<? extends Annotation>> annotationsOnClassesToSecure() {
        return asList(Controller.class);
    }

    @Override
    protected List<Class<? extends Annotation>> methodLevelSecurityAnnotations() {
        return asList(
                PreAuthorize.class,
                PreFilter.class,
                PostAuthorize.class,
                PostFilter.class,
                NotSecured.class
        );
    }

    @Override
    protected List<Class<?>> excludedClasses() {
        return asList(IfsErrorController.class);
    }
}
