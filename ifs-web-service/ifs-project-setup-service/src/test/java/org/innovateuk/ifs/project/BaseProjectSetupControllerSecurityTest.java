package org.innovateuk.ifs.project;

import org.innovateuk.ifs.project.status.security.SetupSectionsPermissionRules;
import org.innovateuk.ifs.security.BaseControllerSecurityTest;
import org.junit.Before;
import org.mockito.Mockito;

import java.util.function.Consumer;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public abstract class BaseProjectSetupControllerSecurityTest<ControllerType> extends BaseControllerSecurityTest<ControllerType> {

    private SetupSectionsPermissionRules permissionRules;

    @Before
    public void lookupPermissionRules() {
        permissionRules = getMockPermissionRulesBean(SetupSectionsPermissionRules.class);
    }

    protected void assertSecured(Runnable invokeControllerFn, Consumer<SetupSectionsPermissionRules> verification) {
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
