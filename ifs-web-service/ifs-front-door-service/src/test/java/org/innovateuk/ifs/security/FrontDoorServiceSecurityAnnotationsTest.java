package org.innovateuk.ifs.security;

import com.google.common.collect.ImmutableList;
import org.innovateuk.ifs.async.controller.endtoend.EndToEndAsyncControllerTestController;

import java.util.List;

import static org.assertj.core.util.Lists.emptyList;

public class FrontDoorServiceSecurityAnnotationsTest extends AbstractWebServiceSecurityAnnotationsTest {

    @Override
    protected List<Class<?>> additionalExcludedClasses() {
        return ImmutableList.of(EndToEndAsyncControllerTestController.class);
    }
}
