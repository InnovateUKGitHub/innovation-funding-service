package org.innovateuk.ifs.commons.security;

import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.innovateuk.ifs.commons.security.evaluator.*;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.After;
import org.junit.Before;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.security.evaluator.CustomPermissionEvaluatorTestUtil.*;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

/**
 * A base class for testing services with Spring Security integrated into them.  PermissionRules-annotated beans are
 * made available as mocks so that we can test the effects of calling service methods against the PermissionRule methods
 * that are available.
 * <p>
 * Subclasses of this base class are therefore able to test the security annotations of their various methods by verifying
 * that individual PermissionRule methods are being called (on their owning mocks)
 */

public abstract class BaseMockSecurityTest extends BaseIntegrationTest {

    @Autowired
    protected GenericApplicationContext applicationContext;

    private Map<Class<?>, Object> mockPermissionRulesBeans;
    private Map<Class<?>, Object> mockPermissionEntityLookupStrategies;

    private PermissionedObjectClassToPermissionsToPermissionsMethods originalRulesMap;
    private PermissionedObjectClassToLookupMethods originalLookupStrategyMap;

    /**
     * Look up a Mockito mock for a given {@link PermissionRules} annotated bean class
     *
     * @param clazz
     * @param <T>
     * @return
     */
    protected <T> T getMockPermissionRulesBean(Class<T> clazz) {
        return (T) mockPermissionRulesBeans.get(clazz);
    }


    /**
     * Look up a Mockito mock for a given {@link PermissionEntityLookupStrategies} annotated bean class
     *
     * @param clazz
     * @param <T>
     * @return
     */
    protected <T> T getMockPermissionEntityLookupStrategiesBean(Class<T> clazz) {
        return (T) mockPermissionEntityLookupStrategies.get(clazz);
    }

    /**
     * Register a temporary bean definition for the class under test (as provided by getClassUnderTest()), and replace
     * all PermissionRules with mocks that can be looked up with getMockPermissionRulesBean().
     */
    @Before
    public void setup() {

        // Process mock annotations
        MockitoAnnotations.initMocks(this);
        swapRealPermissionMethodsForMocks();
        BaseIntegrationTest.setLoggedInUser(newUserResource().build());
    }

    private void swapRealPermissionMethodsForMocks() {

        // get the custom permission evaluator from the applicationContext and swap its rulesMap for one containing only
        // Mockito mocks
        RootCustomPermissionEvaluator permissionEvaluator = (RootCustomPermissionEvaluator) applicationContext.getBean("customPermissionEvaluator");
        cleanDownCachedPermissionRules(permissionEvaluator);

        originalRulesMap = getRulesMap(permissionEvaluator);

        Pair<PermissionRulesClassToMock, PermissionedObjectClassToPermissionsToPermissionsMethods> mocksAndRecorders =
                generateMockedOutRulesMap(originalRulesMap);

        mockPermissionRulesBeans = mocksAndRecorders.getLeft();
        PermissionedObjectClassToPermissionsToPermissionsMethods recordingProxies = mocksAndRecorders.getRight();
        setRulesMap(permissionEvaluator, recordingProxies);

        originalLookupStrategyMap = (PermissionedObjectClassToLookupMethods) ReflectionTestUtils.getField(permissionEvaluator, "lookupStrategyMap");

        Pair<PermissionedObjectClassToMockLookupStrategyClasses, PermissionedObjectClassToLookupMethods> mockedOut = generateMockedOutLookupMap(originalLookupStrategyMap);
        mockPermissionEntityLookupStrategies = mockedOut.getLeft();
        setLookupStrategyMap(permissionEvaluator, mockedOut.getRight());
    }

    /**
     * Replace the original rulesMap and lookup strategy on the custom permission evaluator
     */
    @After
    public void teardown() {

        RootCustomPermissionEvaluator permissionEvaluator = (RootCustomPermissionEvaluator) applicationContext.getBean("customPermissionEvaluator");
        cleanDownCachedPermissionRules(permissionEvaluator);
        setRulesMap(permissionEvaluator, originalRulesMap);
        setLookupStrategyMap(permissionEvaluator, originalLookupStrategyMap);
    }

    private void setLookupStrategyMap(RootCustomPermissionEvaluator permissionEvaluator, PermissionedObjectClassToLookupMethods right) {
        ReflectionTestUtils.setField(permissionEvaluator, "lookupStrategyMap", right);
    }

    protected Pair<PermissionedObjectClassToMockLookupStrategyClasses, PermissionedObjectClassToLookupMethods> generateMockedOutLookupMap(PermissionedObjectClassToLookupMethods originalLookupStrategyMap) {
        final PermissionedObjectClassToMockLookupStrategyClasses mockLookupBeans = new PermissionedObjectClassToMockLookupStrategyClasses();
        final PermissionedObjectClassToLookupMethods newMockLookupMap = new PermissionedObjectClassToLookupMethods();

        for (Entry<Class<?>, ListOfOwnerAndMethod> entry : originalLookupStrategyMap.entrySet()) {
            final Class<?> permissionedObjectClass = entry.getKey();
            final ListOfOwnerAndMethod originalLookups = entry.getValue();
            for (Pair<Object, Method> originalLookup : originalLookups) {
                final Object originalLookupBeans = originalLookup.getLeft();
                final Method originalLookupMethod = originalLookup.getRight();

                if (!mockLookupBeans.containsKey(originalLookupBeans.getClass())) {
                    mockLookupBeans.put(originalLookupBeans.getClass(), mock(originalLookupBeans.getClass()));
                }
                final Object mockLookupBean = mockLookupBeans.get(originalLookupBeans.getClass());

                final String methodName = originalLookupMethod.getName();
                Class<?>[] methodParameters = originalLookupMethod.getParameterTypes();
                final Method mockLookupMethod;

                try {
                    mockLookupMethod = mockLookupBean.getClass().getMethod(methodName, methodParameters);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException("Unable to look up same method on mock", e);
                }
                if (!newMockLookupMap.containsKey(permissionedObjectClass)) {
                    newMockLookupMap.put(permissionedObjectClass, new ListOfOwnerAndMethod());
                }
                final ListOfOwnerAndMethod mockLookups = newMockLookupMap.get(permissionedObjectClass);
                mockLookups.add(Pair.of(mockLookupBean, mockLookupMethod));
            }
        }

        return Pair.of(mockLookupBeans, newMockLookupMap);
    }

    /**
     * Given the rulesMap from the RootCustomPermissionEvaluator, this method replaces all of the original @PermissionRules-annotated beans
     * with Mockito mocks, and all of the @PermissionRule-annotated methods on those beans with the equivalent methods from the mocks.
     * <p>
     * This method then returns all of the mocks that it has created (so for each original @PermissionRules class, there will be an
     * equivalent mock) and the new rulesMap containing the mock replacements.
     *
     * @param originalRulesMap
     * @return
     */
    protected Pair<PermissionRulesClassToMock, PermissionedObjectClassToPermissionsToPermissionsMethods> generateMockedOutRulesMap(
            PermissionedObjectClassToPermissionsToPermissionsMethods originalRulesMap) {

        PermissionRulesClassToMock mockPermissionRulesBeans = new PermissionRulesClassToMock();
        PermissionRulesClassToMock recordingPermissionRulesBeans = new PermissionRulesClassToMock();

        PermissionedObjectClassToPermissionsToPermissionsMethods newMockRulesMap = new PermissionedObjectClassToPermissionsToPermissionsMethods();

        for (Entry<Class<?>, PermissionsToPermissionsMethods> entry : originalRulesMap.entrySet()) {

            Class<?> originalDtoClass = entry.getKey();
            PermissionsToPermissionsMethods originalPermissionBeansAndMethodsByPermission = entry.getValue();

            PermissionsToPermissionsMethods newMockPermissionBeansAndMethodsByPermission = new PermissionsToPermissionsMethods();

            for (Entry<String, ListOfOwnerAndMethod> originalPermissionBeansAndMethods : originalPermissionBeansAndMethodsByPermission.entrySet()) {

                String originalPermission = originalPermissionBeansAndMethods.getKey();
                ListOfOwnerAndMethod originalListOfPermissionMethods = originalPermissionBeansAndMethods.getValue();
                ListOfOwnerAndMethod newRecordingListOfPermissionMethods = new ListOfOwnerAndMethod();

                for (Pair<Object, Method> beanAndPermissionMethods : originalListOfPermissionMethods) {

                    Object originalPermissionRulesBean = beanAndPermissionMethods.getKey();

                    if (!mockPermissionRulesBeans.containsKey(originalPermissionRulesBean.getClass())) {
                        Object mock = mock(originalPermissionRulesBean.getClass());
                        Object decoratedMock = createPermissionRuleMock(mock, originalPermissionRulesBean.getClass());
                        mockPermissionRulesBeans.put(originalPermissionRulesBean.getClass(), mock);
                        recordingPermissionRulesBeans.put(originalPermissionRulesBean.getClass(), decoratedMock);
                    }

                    final Object recordingPermissionsRulesBean = recordingPermissionRulesBeans.get(originalPermissionRulesBean.getClass());
                    Method originalPermissionMethod = beanAndPermissionMethods.getRight();
                    String methodName = originalPermissionMethod.getName();
                    Class<?>[] methodParameters = originalPermissionMethod.getParameterTypes();
                    final Method recordingPermissionMethod;

                    try {
                        recordingPermissionMethod = recordingPermissionsRulesBean.getClass().getMethod(methodName, methodParameters);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException("Unable to look up same method on mock", e);
                    }

                    newRecordingListOfPermissionMethods.add(Pair.of(recordingPermissionsRulesBean, recordingPermissionMethod));
                }

                newMockPermissionBeansAndMethodsByPermission.put(originalPermission, newRecordingListOfPermissionMethods);
            }

            newMockRulesMap.put(originalDtoClass, newMockPermissionBeansAndMethodsByPermission);
        }

        return Pair.of(mockPermissionRulesBeans, newMockRulesMap);
    }

    protected Object createPermissionRuleMock(Object mock, Class<?> mockClass) {
        return mock;
    }

    protected static void assertAccessDenied(Runnable serviceCall, Runnable verifications) {
        try {
            serviceCall.run();
            fail("Expected an AccessDeniedException");
        } catch (AccessDeniedException e) {
            verifications.run();
        }
    }

    protected void assertPostFilter(List list, Runnable verifications) {
        assertTrue(list.isEmpty());
        verifications.run();
    }

    /**
     * Asserts that only the given Global Role(s) can perform the given action.  Any specified roles who cannot perform
     * the action will raise an error and vice versa any not specified who can will also raise an error
     */
    protected void assertRolesCanPerform(Runnable actionFn, Role... supportedRoles) {
        assertRolesCanPerform(actionFn, asList(supportedRoles));
    }

    /**
     * Asserts that only the given Global Role(s) can perform the given action.  Any specified roles who cannot perform
     * the action will raise an error and vice versa any not specified who can will also raise an error
     */
    protected void assertRolesCanPerform(Runnable actionFn, List<Role> supportedRoles) {

        asList(Role.values()).forEach(role -> {

            UserResource userWithRole = newUserResource().withRolesGlobal(singletonList(role)).build();
            setLoggedInUser(userWithRole);

            if (supportedRoles.contains(role)) {
                actionFn.run();
            } else {
                try {
                    actionFn.run();
                    fail("Should have thrown an AccessDeniedException for any non " + supportedRoles + " users, " +
                            "but succeeded with " + role);
                } catch (AccessDeniedException e) {
                    // expected behaviour
                }
            }
        });
    }

    public static class PermissionRulesClassToMock extends HashMap<Class<?>, Object> {
    }

    public static class PermissionedObjectClassToMockLookupStrategyClasses extends HashMap<Class<?>, Object> {
    }
}
