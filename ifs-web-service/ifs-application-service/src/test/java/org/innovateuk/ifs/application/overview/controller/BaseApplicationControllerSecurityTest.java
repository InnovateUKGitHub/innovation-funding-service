package org.innovateuk.ifs.application.overview.controller;

import org.innovateuk.ifs.security.BaseControllerSecurityTest;
import org.mockito.Mockito;

import java.util.function.Consumer;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public abstract class BaseApplicationControllerSecurityTest<ControllerType> extends BaseControllerSecurityTest<ControllerType> {

    protected <T> void assertSecured(Runnable invokeControllerFn, Consumer<T> verification, Class clazz) {

        T permissionRules = (T) getMockPermissionRulesBean(clazz);

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
