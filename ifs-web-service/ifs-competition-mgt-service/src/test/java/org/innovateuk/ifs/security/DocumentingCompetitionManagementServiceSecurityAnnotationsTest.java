package org.innovateuk.ifs.security;

import java.util.List;

import static java.util.Collections.emptyList;

public class DocumentingCompetitionManagementServiceSecurityAnnotationsTest extends AbstractDocumentingWebServiceSecurityAnnotationsTest {

    @Override
    protected List<Class<?>> additionalExcludedClasses() {
        return emptyList();
    }
}
