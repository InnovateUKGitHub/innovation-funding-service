package org.innovateuk.ifs.commons.security.evaluator;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.security.authentication.user.UserAuthentication;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class DefaultPermissionMethodHandlerTest {

    private PermissionedObjectClassToPermissionsToPermissionsMethods permissionMethods =
            new PermissionedObjectClassToPermissionsToPermissionsMethods();

    private DefaultPermissionMethodHandlerTest permissionMethodOwnerMock = mock(DefaultPermissionMethodHandlerTest.class);
    private PermissionsToPermissionsMethods applicationPermissionMethods = new PermissionsToPermissionsMethods();
    private ListOfOwnerAndMethod applicationReadPermissionMethods = new ListOfOwnerAndMethod();
    private UserAuthentication authentication = new UserAuthentication(newUserResource().build());
    private ApplicationResource application = newApplicationResource().build();

    private Pair<Object, Method> readApplicationPermissionMethod1 = Pair.of(permissionMethodOwnerMock,
            getClass().getDeclaredMethod("readApplicationPermissionMethod1", ApplicationResource.class, UserResource.class));

    private Pair<Object, Method> readApplicationPermissionMethod2 = Pair.of(permissionMethodOwnerMock,
            getClass().getDeclaredMethod("readApplicationPermissionMethod2", ApplicationResource.class, UserResource.class));

    private Pair<Object, Method> readApplicationPermissionMethod3 = Pair.of(permissionMethodOwnerMock,
            getClass().getDeclaredMethod("readApplicationPermissionMethod3", ApplicationResource.class, UserResource.class));

    public DefaultPermissionMethodHandlerTest() throws NoSuchMethodException {
    }

    @Before
    public void setup() throws NoSuchMethodException {

        // add "Read ApplicationResource" permission methods to handler
        applicationReadPermissionMethods.add(readApplicationPermissionMethod1);
        applicationReadPermissionMethods.add(readApplicationPermissionMethod2);
        applicationReadPermissionMethods.add(readApplicationPermissionMethod3);

        applicationPermissionMethods.put("READ", applicationReadPermissionMethods);

        permissionMethods.put(ApplicationResource.class, applicationPermissionMethods);
    }

    @Test
    public void testPermissionCheckerRunsThroughAllPermissionMethods() {

        DefaultPermissionMethodHandler handler = new DefaultPermissionMethodHandler(permissionMethods);

        when(permissionMethodOwnerMock.readApplicationPermissionMethod1(application, authentication.getDetails())).thenReturn(false);
        when(permissionMethodOwnerMock.readApplicationPermissionMethod2(application, authentication.getDetails())).thenReturn(false);
        when(permissionMethodOwnerMock.readApplicationPermissionMethod3(application, authentication.getDetails())).thenReturn(false);

        assertFalse(handler.hasPermission(authentication, application, "READ", ApplicationResource.class));

        verify(permissionMethodOwnerMock).readApplicationPermissionMethod1(application, authentication.getDetails());
        verify(permissionMethodOwnerMock).readApplicationPermissionMethod2(application, authentication.getDetails());
        verify(permissionMethodOwnerMock).readApplicationPermissionMethod3(application, authentication.getDetails());
    }

    @Test
    public void testStopPermissionCheckingAfterFirstMethodThatReturnsTrue() {

        DefaultPermissionMethodHandler handler = new DefaultPermissionMethodHandler(permissionMethods);

        when(permissionMethodOwnerMock.readApplicationPermissionMethod1(application, authentication.getDetails())).thenReturn(false);
        when(permissionMethodOwnerMock.readApplicationPermissionMethod2(application, authentication.getDetails())).thenReturn(true);

        assertTrue(handler.hasPermission(authentication, application, "READ", ApplicationResource.class));

        verify(permissionMethodOwnerMock).readApplicationPermissionMethod1(application, authentication.getDetails());
        verify(permissionMethodOwnerMock).readApplicationPermissionMethod2(application, authentication.getDetails());
        verify(permissionMethodOwnerMock, never()).readApplicationPermissionMethod3(application, authentication.getDetails());
    }

    public boolean readApplicationPermissionMethod1(ApplicationResource application, UserResource user) {
        return false;
    }

    public boolean readApplicationPermissionMethod2(ApplicationResource application, UserResource user) {
        return false;
    }

    public boolean readApplicationPermissionMethod3(ApplicationResource application, UserResource user) {
        return false;
    }
}
