package org.innovateuk.ifs.commons.security;

import org.innovateuk.ifs.security.AbstractDataServiceSecurityAnnotationsTest;

import java.util.List;

import static org.assertj.core.util.Lists.emptyList;

public class DataServiceSecurityAnnotationsTest extends AbstractDataServiceSecurityAnnotationsTest {

    @Override
    protected List<Class<?>> additionalExcludedClasses() {
        return emptyList();
    }


}
