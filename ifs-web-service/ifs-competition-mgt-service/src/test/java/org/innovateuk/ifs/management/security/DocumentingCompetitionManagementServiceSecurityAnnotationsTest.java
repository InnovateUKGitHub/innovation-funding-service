package org.innovateuk.ifs.management.security;

import org.innovateuk.ifs.security.AbstractDocumentingWebServiceSecurityAnnotationsTest;import java.util.List;

import static java.util.Collections.emptyList;

public class DocumentingCompetitionManagementServiceSecurityAnnotationsTest extends AbstractDocumentingWebServiceSecurityAnnotationsTest {

    @Override
    protected List<Class<?>> additionalExcludedClasses() {
        return emptyList();
    }
}
