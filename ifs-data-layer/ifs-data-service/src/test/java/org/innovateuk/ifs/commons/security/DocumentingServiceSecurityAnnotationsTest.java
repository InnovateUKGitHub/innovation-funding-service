package org.innovateuk.ifs.commons.security;

import org.innovateuk.ifs.commons.AbstractDocumentingServiceSecurityAnnotationsTest;
import org.innovateuk.ifs.commons.security.evaluator.RootCustomPermissionEvaluator;
import org.innovateuk.ifs.security.StatelessAuthenticationFilter;
import org.innovateuk.ifs.security.UidAuthenticationService;
import org.innovateuk.ifs.security.evaluator.CustomPermissionEvaluator;

import java.util.List;

import static java.util.Arrays.asList;

public class DocumentingServiceSecurityAnnotationsTest extends AbstractDocumentingServiceSecurityAnnotationsTest {

    @Override
    protected List<Class<?>> excludedClasses() {
        return asList(
                UidAuthenticationService.class,
                StatelessAuthenticationFilter.class
        );
    }

    @Override
    protected RootCustomPermissionEvaluator evaluator() {
        return (CustomPermissionEvaluator) context.getBean("customPermissionEvaluator");

    }
}
