package org.innovateuk.ifs.commons.security;

import org.innovateuk.ifs.security.AbstractDocumentingDataServiceSecurityAnnotationsTest;

import java.lang.annotation.Annotation;
import java.util.List;

import static java.util.Collections.emptyList;


public class DocumentingDataServiceSecurityAnnotationsTest extends AbstractDocumentingDataServiceSecurityAnnotationsTest {

    @Override
    protected List<Class<?>> additionalExcludedClasses() {
        return emptyList();
    }
}
