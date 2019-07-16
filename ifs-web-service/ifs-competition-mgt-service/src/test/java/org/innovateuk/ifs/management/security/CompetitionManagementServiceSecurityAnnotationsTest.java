package org.innovateuk.ifs.management.security;

import org.innovateuk.ifs.async.controller.endtoend.EndToEndAsyncControllerIntegrationTest;
import org.innovateuk.ifs.async.controller.endtoend.EndToEndAsyncControllerTestController;
import org.innovateuk.ifs.async.controller.endtoend.EndToEndAsyncControllerTestService;import org.innovateuk.ifs.security.AbstractWebServiceSecurityAnnotationsTest;

import java.util.List;

import static java.util.Arrays.asList;

public class CompetitionManagementServiceSecurityAnnotationsTest extends AbstractWebServiceSecurityAnnotationsTest {

    @Override
    protected List<Class<?>> additionalExcludedClasses() {
        return additionalClasses();
    }

    protected List<Class<?>> additionalClasses() {
        return asList(EndToEndAsyncControllerTestController.class,
                EndToEndAsyncControllerIntegrationTest.class,
                EndToEndAsyncControllerTestService.class);
    }
}
