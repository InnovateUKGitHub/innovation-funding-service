package org.innovateuk.ifs.security;

import java.util.List;

import static org.assertj.core.util.Lists.emptyList;

public class ProjectSetupManagementServiceSecurityAnnotationsTest extends AbstractWebServiceSecurityAnnotationsTest {


    @Override
    protected List<Class<?>> additionalExcludedClasses() {
        return emptyList();
    }
}
