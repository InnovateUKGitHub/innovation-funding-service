package com.worth.ifs.project;

import com.worth.ifs.security.BaseControllerSecurityTest;
import org.junit.Before;
import org.mockito.Mockito;

import java.util.function.Consumer;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public abstract class BaseProjectSetupControllerSecurityTest<ControllerType> extends BaseControllerSecurityTest<ControllerType> {

    private ProjectSetupSectionsPermissionRules permissionRules;

    @Before
    public void lookupPermissionRules() {
        permissionRules = getMockPermissionRulesBean(ProjectSetupSectionsPermissionRules.class);
    }

    protected void assertSecured(Runnable invokeControllerFn) {
        assertAccessDenied(
                invokeControllerFn::run,
                () -> {
                    getVerification().accept(verify(permissionRules, times(1)));
                    verifyNoMoreInteractions(permissionRules);
                    Mockito.reset(permissionRules);
                }
        );
    }

    protected abstract Consumer<ProjectSetupSectionsPermissionRules> getVerification();
}
