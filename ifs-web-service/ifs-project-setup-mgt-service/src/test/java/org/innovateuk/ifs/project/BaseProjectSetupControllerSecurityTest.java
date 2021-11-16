package org.innovateuk.ifs.project;

import org.innovateuk.ifs.project.status.security.SetupSectionsPermissionRules;
import org.innovateuk.ifs.security.BaseControllerSecurityTest;
import org.junit.Before;
import org.springframework.test.context.TestPropertySource;

import java.util.function.Consumer;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@TestPropertySource(properties = { "ifs.loan.partb.enabled=true" })
public abstract class BaseProjectSetupControllerSecurityTest<ControllerType> extends BaseControllerSecurityTest<ControllerType> {

    private SetupSectionsPermissionRules permissionRules;

    @Before
    public void lookupPermissionRules() {
        permissionRules = getMockPermissionRulesBean(SetupSectionsPermissionRules.class);
    }

    protected void assertSecured(Runnable invokeControllerFn) {
        assertAccessDenied(
                invokeControllerFn::run,
                () -> getVerification().accept(verify(permissionRules, times(1)))
        );
    }

    protected void assertSecured(Runnable invokeControllerFn, Consumer<SetupSectionsPermissionRules> verification) {
        assertAccessDenied(
                invokeControllerFn::run,
                () -> verification.accept(verify(permissionRules, times(1)))
        );
    }

    protected abstract Consumer<SetupSectionsPermissionRules> getVerification();
}
