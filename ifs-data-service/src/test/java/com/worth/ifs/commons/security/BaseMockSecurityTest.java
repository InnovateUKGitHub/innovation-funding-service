package com.worth.ifs.commons.security;

import com.worth.ifs.commons.BaseIntegrationTest;
import com.worth.ifs.commons.security.CustomPermissionEvaluator.ListOfOwnerAndMethod;
import com.worth.ifs.commons.security.CustomPermissionEvaluator.PermissionedObjectClassToPermissionsToPermissionsMethods;
import com.worth.ifs.commons.security.CustomPermissionEvaluator.PermissionedObjectClassesToListOfLookup;
import com.worth.ifs.commons.security.CustomPermissionEvaluator.PermissionsToPermissionsMethods;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.Before;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.security.access.AccessDeniedException;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.setField;

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
    private PermissionedObjectClassesToListOfLookup originalLookupStrategyMap;

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

        // get the custom permission evaluator from the applicationContext and swap its rulesMap for one containing only
        // Mockito mocks
        CustomPermissionEvaluator permissionEvaluator = (CustomPermissionEvaluator) applicationContext.getBean("customPermissionEvaluator");
            originalRulesMap = (PermissionedObjectClassToPermissionsToPermissionsMethods) getField(permissionEvaluator, "rulesMap");

        Pair<PermissionRulesClassToMock, PermissionedObjectClassToPermissionsToPermissionsMethods> mocksAndRecorders =
                generateMockedOutRulesMap(originalRulesMap);

        PermissionRulesClassToMock mocks = mocksAndRecorders.getLeft();
        PermissionedObjectClassToPermissionsToPermissionsMethods recordingProxies = mocksAndRecorders.getRight();

        mockPermissionRulesBeans = mocks;
        setRuleMap(permissionEvaluator, recordingProxies);

        originalLookupStrategyMap = (PermissionedObjectClassesToListOfLookup) getField(permissionEvaluator, "lookupStrategyMap");

        Pair<PermissionedObjectClassToMockLookupStrategyClasses, PermissionedObjectClassesToListOfLookup> mockedOut = generateMockedOutLookupMap(originalLookupStrategyMap);
        mockPermissionEntityLookupStrategies = mockedOut.getLeft();
        setLookupStrategyMap(permissionEvaluator, mockedOut.getRight());

        setLoggedInUser(newUserResource().build());
    }

    /**
     * Replace the original rulesMap and lookup strategy on the custom permission evaluator
     */
    @After
    public void teardown() {
        CustomPermissionEvaluator permissionEvaluator = (CustomPermissionEvaluator) applicationContext.getBean("customPermissionEvaluator");
        setRuleMap(permissionEvaluator, originalRulesMap);
        setLookupStrategyMap(permissionEvaluator, originalLookupStrategyMap);
    }

    private void setLookupStrategyMap(CustomPermissionEvaluator permissionEvaluator, PermissionedObjectClassesToListOfLookup right) {
        setField(permissionEvaluator, "lookupStrategyMap", right);
    }

    private void setRuleMap(CustomPermissionEvaluator permissionEvaluator, PermissionedObjectClassToPermissionsToPermissionsMethods recordingProxies) {
        setField(permissionEvaluator, "rulesMap", recordingProxies);
    }

    protected Pair<PermissionedObjectClassToMockLookupStrategyClasses, PermissionedObjectClassesToListOfLookup> generateMockedOutLookupMap(PermissionedObjectClassesToListOfLookup originalLookupStrategyMap) {
        final PermissionedObjectClassToMockLookupStrategyClasses mockLookupBeans = new PermissionedObjectClassToMockLookupStrategyClasses();
        final PermissionedObjectClassesToListOfLookup newMockLookupMap = new PermissionedObjectClassesToListOfLookup();

        for (Entry<Class<?>, ListOfOwnerAndMethod> entry : originalLookupStrategyMap.entrySet()) {
            final Class<?> permissionedObjectClass = entry.getKey();
            final ListOfOwnerAndMethod originalLookups = entry.getValue();
            for (Pair<Object, Method> originalLookup: originalLookups) {
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
     * Given the rulesMap from the CustomPermissionEvaluator, this method replaces all of the original @PermissionRules-annotated beans
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

    protected void assertAccessDenied(Runnable serviceCall, Runnable verifications) {
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

    public static class PermissionRulesClassToMock extends HashMap<Class<?>, Object> {}

    public static class PermissionedObjectClassToMockLookupStrategyClasses extends HashMap<Class<?>, Object> {}
}
