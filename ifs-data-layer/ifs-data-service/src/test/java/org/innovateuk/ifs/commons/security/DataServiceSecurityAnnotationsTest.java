package org.innovateuk.ifs.commons.security;

import org.innovateuk.ifs.security.AbstractDataServiceSecurityAnnotationsTest;
import org.innovateuk.ifs.security.StatelessAuthenticationFilter;

import java.util.List;

import static java.util.Arrays.asList;

public class DataServiceSecurityAnnotationsTest extends AbstractDataServiceSecurityAnnotationsTest {

    @Override
    protected List<Class<?>> additionalExcludedClasses() {
        return asList(StatelessAuthenticationFilter.class);
    }

}
