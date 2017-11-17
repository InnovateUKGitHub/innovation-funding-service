package org.innovateuk.ifs.security;

import org.innovateuk.ifs.commons.AbstractServiceSecurityAnnotationsTest;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.security.evaluator.RootCustomPermissionEvaluator;
import org.innovateuk.ifs.security.evaluator.CustomPermissionEvaluator;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.stereotype.Service;

import java.lang.annotation.Annotation;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.util.Lists.emptyList;
import static org.innovateuk.ifs.util.CollectionFunctions.union;

public abstract class AbstractDataServiceSecurityAnnotationsTest extends AbstractServiceSecurityAnnotationsTest {


    @Override
    protected final List<Class<?>> excludedClasses() {
        return union(asList(UidAuthenticationService.class, StatelessAuthenticationFilter.class), additionalExcludedClasses());
    }

    @Override
    protected final List<Class<? extends Annotation>> classLevelSecurityAnnotations() {
        return emptyList();
    }

    @Override
    protected final List<Class<? extends Annotation>> annotationsOnClassesToSecure() {
        return asList(Service.class);
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


    /**
     * Add classes which are not present in this base project but still need to be ignored.
     * @return
     */
    protected abstract List<Class<?>> additionalExcludedClasses();

}
