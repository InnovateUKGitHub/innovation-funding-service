package org.innovateuk.ifs.security;

import org.innovateuk.ifs.benchmark.BenchmarkController;

import java.util.List;

import static java.util.Collections.singletonList;

public class DocumentingApplicationServiceSecurityAnnotationsTest extends AbstractDocumentingWebServiceSecurityAnnotationsTest {

    @Override
    protected List<Class<?>> additionalExcludedClasses() {
        return singletonList(BenchmarkController.class);
    }
}
