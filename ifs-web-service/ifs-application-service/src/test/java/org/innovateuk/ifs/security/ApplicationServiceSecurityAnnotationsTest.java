package org.innovateuk.ifs.security;

import org.innovateuk.ifs.async.controller.endtoend.EndToEndAsyncControllerIntegrationTest;
import org.innovateuk.ifs.async.controller.endtoend.EndToEndAsyncControllerTestController;
import org.innovateuk.ifs.async.controller.endtoend.EndToEndAsyncControllerTestService;
import org.innovateuk.ifs.benchmark.BenchmarkController;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.util.CollectionFunctions.union;

public class ApplicationServiceSecurityAnnotationsTest extends AbstractWebServiceSecurityAnnotationsTest {

    @Override
    protected List<Class<?>> additionalExcludedClasses() {
        return union(asList(BenchmarkController.class), additionalClasses());
    }

    protected List<Class<?>> additionalClasses() {
        return asList(EndToEndAsyncControllerTestController.class,
                EndToEndAsyncControllerIntegrationTest.class,
                EndToEndAsyncControllerTestService.class);
    }
}
