package org.innovateuk.ifs.security;

import org.assertj.core.util.Lists;

import java.util.List;

public class AssessmentServiceSecurityAnnotationsTest extends AbstractWebServiceSecurityAnnotationsTest {

    @Override
    protected List<Class<?>> additionalExcludedClasses() {
        return Lists.emptyList();
    }
}
