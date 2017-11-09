package org.innovateuk.ifs.commons.security;

import org.innovateuk.ifs.commons.AbstractServiceSecurityAnnotationsTest;
import org.innovateuk.ifs.security.StatelessAuthenticationFilter;
import org.innovateuk.ifs.security.UidAuthenticationService;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.stereotype.Service;

import java.lang.annotation.Annotation;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.util.Lists.emptyList;

public class ServiceSecurityAnnotationsTest extends AbstractServiceSecurityAnnotationsTest {

    @Override
    protected List<Class<?>> excludedClasses() {
        return asList(
                UidAuthenticationService.class,
                StatelessAuthenticationFilter.class
        );
    }


    @Override
    protected List<Class<? extends Annotation>> classLevelSecurityAnnotations() {
        return emptyList();
    }

    @Override
    protected List<Class<? extends Annotation>> annotationsOnClassesToSecure() {
        return asList(Service.class);
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

}
