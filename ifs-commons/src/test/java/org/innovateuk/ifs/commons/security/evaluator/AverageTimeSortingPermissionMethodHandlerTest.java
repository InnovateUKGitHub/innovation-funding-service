package org.innovateuk.ifs.commons.security.evaluator;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.security.authentication.user.UserAuthentication;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Supplier;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AverageTimeSortingPermissionMethodHandlerTest {

    private PermissionedObjectClassToPermissionsToPermissionsMethods permissionMethods =
            new PermissionedObjectClassToPermissionsToPermissionsMethods();

    private AverageTimeSortingPermissionMethodHandlerTest permissionMethodOwnerMock = mock(AverageTimeSortingPermissionMethodHandlerTest.class);
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

    public AverageTimeSortingPermissionMethodHandlerTest() throws NoSuchMethodException {
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
    public void testCountingAverageResponseTimesSingleCallPerMethod() {

        AverageTimeSortingPermissionMethodHandler handler = new AverageTimeSortingPermissionMethodHandler(permissionMethods);
        AverageTimeSortingPermissionMethodHandler.Sampler sampler = mock(AverageTimeSortingPermissionMethodHandler.Sampler.class);
        ReflectionTestUtils.setField(handler, "sampler", sampler);

        when(sampler.isTakeSample()).thenReturn(true);
        when(sampler.hasPermissionWithTiming(isA(Supplier.class))).
                thenReturn(Pair.of(false, 50L)).
                thenReturn(Pair.of(false, 10L)).
                thenReturn(Pair.of(false, 25L));

        assertFalse(handler.hasPermission(authentication, application, "READ", ApplicationResource.class));

        assertPermissionMethodTimings(readApplicationPermissionMethod1, 50L, 1L, handler);
        assertPermissionMethodTimings(readApplicationPermissionMethod2, 10L, 1L, handler);
        assertPermissionMethodTimings(readApplicationPermissionMethod3, 25L, 1L, handler);
    }

    @Test
    public void testCountingAverageResponseTimesMultipleCallsPerMethod() {

        AverageTimeSortingPermissionMethodHandler handler = new AverageTimeSortingPermissionMethodHandler(permissionMethods);
        AverageTimeSortingPermissionMethodHandler.Sampler sampler = mock(AverageTimeSortingPermissionMethodHandler.Sampler.class);
        ReflectionTestUtils.setField(handler, "sampler", sampler);

        when(sampler.isTakeSample()).thenReturn(true);
        when(sampler.hasPermissionWithTiming(isA(Supplier.class))).
                thenReturn(Pair.of(false, 50L)).
                thenReturn(Pair.of(false, 10L)).
                thenReturn(Pair.of(false, 25L)).
                thenReturn(Pair.of(false, 50L)).
                thenReturn(Pair.of(false, 50L)).
                thenReturn(Pair.of(true, 50L));

        assertFalse(handler.hasPermission(authentication, application, "READ", ApplicationResource.class));
        assertTrue(handler.hasPermission(authentication, application, "READ", ApplicationResource.class));

        assertPermissionMethodTimings(readApplicationPermissionMethod1, 50L, 2L, handler);
        assertPermissionMethodTimings(readApplicationPermissionMethod2, 30L, 2L, handler);
        assertPermissionMethodTimings(readApplicationPermissionMethod3, 37L, 2L, handler);
    }

    @Test
    public void testCountingAverageResponseTimesOnlyRecordsWhenSamplerDecidesTo() {

        AverageTimeSortingPermissionMethodHandler handler = new AverageTimeSortingPermissionMethodHandler(permissionMethods);
        AverageTimeSortingPermissionMethodHandler.Sampler sampler = mock(AverageTimeSortingPermissionMethodHandler.Sampler.class);
        ReflectionTestUtils.setField(handler, "sampler", sampler);

        when(sampler.isTakeSample()).
                thenReturn(true).
                thenReturn(false).
                thenReturn(true).
                thenReturn(true).
                thenReturn(false).
                thenReturn(false);

        when(sampler.hasPermissionWithTiming(isA(Supplier.class))).
                thenReturn(Pair.of(false, 50L)). // applied to method1
                thenReturn(Pair.of(false, 10L)). // applied to method3
                thenReturn(Pair.of(false, 25L)); // applied to method2 - after the initial set of methods have been reordered

        when(permissionMethodOwnerMock.readApplicationPermissionMethod2(application, authentication.getDetails())).thenReturn(false);
        when(permissionMethodOwnerMock.readApplicationPermissionMethod3(application, authentication.getDetails())).thenReturn(false);

        assertFalse(handler.hasPermission(authentication, application, "READ", ApplicationResource.class));
        assertFalse(handler.hasPermission(authentication, application, "READ", ApplicationResource.class));

        assertPermissionMethodTimings(readApplicationPermissionMethod1, 50L, 1L, handler);
        assertPermissionMethodTimings(readApplicationPermissionMethod2, 25L, 1L, handler);
        assertPermissionMethodTimings(readApplicationPermissionMethod3, 10L, 1L, handler);
    }

    @Test
    public void testPermissionCheckerRunsThroughAllPermissionMethods() {

        AverageTimeSortingPermissionMethodHandler handler = new AverageTimeSortingPermissionMethodHandler(permissionMethods);

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

        AverageTimeSortingPermissionMethodHandler handler = new AverageTimeSortingPermissionMethodHandler(permissionMethods);

        when(permissionMethodOwnerMock.readApplicationPermissionMethod1(application, authentication.getDetails())).thenReturn(false);
        when(permissionMethodOwnerMock.readApplicationPermissionMethod2(application, authentication.getDetails())).thenReturn(true);

        assertTrue(handler.hasPermission(authentication, application, "READ", ApplicationResource.class));

        verify(permissionMethodOwnerMock).readApplicationPermissionMethod1(application, authentication.getDetails());
        verify(permissionMethodOwnerMock).readApplicationPermissionMethod2(application, authentication.getDetails());
        verify(permissionMethodOwnerMock, never()).readApplicationPermissionMethod3(application, authentication.getDetails());
    }

    @Test
    public void testOrderingOfPermissionCheckingMethodsByAverageTime() {

        AverageTimeSortingPermissionMethodHandler handler = new AverageTimeSortingPermissionMethodHandler(permissionMethods);
        AverageTimeSortingPermissionMethodHandler.Sampler sampler = mock(AverageTimeSortingPermissionMethodHandler.Sampler.class);
        ReflectionTestUtils.setField(handler, "sampler", sampler);

        when(sampler.isTakeSample()).thenReturn(true);

        when(sampler.hasPermissionWithTiming(isA(Supplier.class))).
                thenReturn(Pair.of(false, 50L)).
                thenReturn(Pair.of(false, 10L)).
                thenReturn(Pair.of(false, 25L));

        assertFalse(handler.hasPermission(authentication, application, "READ", ApplicationResource.class));

        assertPermissionMethodTimings(readApplicationPermissionMethod1, 50L, 1L, handler);
        assertPermissionMethodTimings(readApplicationPermissionMethod2, 10L, 1L, handler);
        assertPermissionMethodTimings(readApplicationPermissionMethod3, 25L, 1L, handler);

        // After this initial run, the order should be resorted to promote faster permission methods closer to the start.
        // Therefore, we can now supply some new timings from the Sampler and they should be applied to method2,
        // method3 and method1 in the new order
        when(sampler.hasPermissionWithTiming(isA(Supplier.class))).
                thenReturn(Pair.of(false, 10L)).
                thenReturn(Pair.of(false, 20L)).
                thenReturn(Pair.of(false, 30L));

        assertFalse(handler.hasPermission(authentication, application, "READ", ApplicationResource.class));

        // and so this list of new average times proves that the first run of handler.hasPermission() sorted the
        // order of execution of permission methods into a new order, so that the new mock responses from
        // sampler.hasPermissionWithTiming() will now record average times against the permission methods in the new order
        assertPermissionMethodTimings(readApplicationPermissionMethod1, 40L, 2L, handler);
        assertPermissionMethodTimings(readApplicationPermissionMethod2, 10L, 2L, handler);
        assertPermissionMethodTimings(readApplicationPermissionMethod3, 22L, 2L, handler);
    }

    @Test
    public void testCountOnMethodCallOverflowingLongMaxValue() {

        AverageTimeSortingPermissionMethodHandler handler = new AverageTimeSortingPermissionMethodHandler(permissionMethods);
        AverageTimeSortingPermissionMethodHandler.Sampler sampler = mock(AverageTimeSortingPermissionMethodHandler.Sampler.class);
        ReflectionTestUtils.setField(handler, "sampler", sampler);

        Map<Pair<Object, Method>, Pair<Long, Long>> permissionResponseTimes = getPermissionResponseTimes(handler);

        // start off our average count for readApplicationPermissionMethod1 at 9998
        permissionResponseTimes.put(readApplicationPermissionMethod1, Pair.of(5000L, 9998L));

        when(sampler.isTakeSample()).thenReturn(true);

        when(sampler.hasPermissionWithTiming(isA(Supplier.class))).
                thenReturn(Pair.of(true, 4000L));

        assertTrue(handler.hasPermission(authentication, application, "READ", ApplicationResource.class));

        assertPermissionMethodTimings(readApplicationPermissionMethod1, 4999L, 9999L, handler);
        assertNoPermissionMethodTimingsRecordedYet(readApplicationPermissionMethod2, handler);
        assertNoPermissionMethodTimingsRecordedYet(readApplicationPermissionMethod3, handler);

        // now call a second time so that the overflow value (10,000) is achieved
        when(sampler.hasPermissionWithTiming(isA(Supplier.class))).
                thenReturn(Pair.of(false, 20L)).
                thenReturn(Pair.of(false, 30L)).
                thenReturn(Pair.of(true, 100L));

        assertTrue(handler.hasPermission(authentication, application, "READ", ApplicationResource.class));

        assertPermissionMethodTimings(readApplicationPermissionMethod1, 4999L, 1L, handler);
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

    private void assertPermissionMethodTimings(Pair<Object, Method> method, Long expectedAverage, Long expectedCount,
                                               AverageTimeSortingPermissionMethodHandler handler) {

        Map<Pair<Object, Method>, Pair<Long, Long>> permissionResponseTimes = getPermissionResponseTimes(handler);

        Pair<Long, Long> permissionMethodTiming = permissionResponseTimes.get(method);
        Long average = permissionMethodTiming.getKey();
        Long count = permissionMethodTiming.getValue();
        assertEquals(expectedAverage, average);
        assertEquals(expectedCount, count);
    }

    private void assertNoPermissionMethodTimingsRecordedYet(Pair<Object, Method> method, AverageTimeSortingPermissionMethodHandler handler) {
        Map<Pair<Object, Method>, Pair<Long, Long>> permissionResponseTimes = getPermissionResponseTimes(handler);
        assertNull(permissionResponseTimes.get(method));
    }

    private Map<Pair<Object, Method>, Pair<Long, Long>> getPermissionResponseTimes(AverageTimeSortingPermissionMethodHandler handler) {
        return (Map<Pair<Object, Method>, Pair<Long, Long>>) ReflectionTestUtils.getField(handler, "averageResponseTimesPerPermissionCheck");
    }
}
