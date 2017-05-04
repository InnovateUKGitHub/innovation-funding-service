package org.innovateuk.ifs.application;

import org.innovateuk.ifs.application.security.ApplicationPermissionRules;
import org.innovateuk.ifs.security.BaseControllerSecurityTest;
import org.junit.Before;
import org.mockito.Mockito;

import java.util.function.Consumer;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public abstract class BaseApplicationControllerSecurityTest<ControllerType> extends BaseControllerSecurityTest<ControllerType> {

    private ApplicationPermissionRules permissionRules;

    @Before
    public void lookupPermissionRules() {
        permissionRules = getMockPermissionRulesBean(ApplicationPermissionRules.class);
    }

    protected void assertSecured(Runnable invokeControllerFn, Consumer<ApplicationPermissionRules> verification) {
        assertAccessDenied(
                invokeControllerFn::run,
                () -> {
                    verification.accept(verify(permissionRules, times(1)));
                    verifyNoMoreInteractions(permissionRules);
                    Mockito.reset(permissionRules);
                }
        );
    }
}
