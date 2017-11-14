package org.innovateuk.ifs.security;

import org.assertj.core.util.Lists;

import java.util.List;

import static org.assertj.core.util.Lists.emptyList;

public class ProjectSetupServiceSecurityAnnotationsTest extends AbstractWebServiceSecurityAnnotationsTest {

    @Override
    protected List<Class<?>> additionalExcludedClasses() {
        return emptyList();
    }
}
