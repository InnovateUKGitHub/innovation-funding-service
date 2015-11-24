package com.worth.ifs;

import com.worth.ifs.commons.security.UserAuthentication;
import com.worth.ifs.security.CustomPermissionEvaluator;
import com.worth.ifs.user.domain.User;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.Before;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.worth.ifs.user.builder.UserBuilder.newUser;
import static org.mockito.Mockito.mock;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.setField;

/**
 * A base class for testing services with Spring Security integrated into them.  PermissionRules-annotated beans are
 * made available as mocks so that we can test the effects of calling service methods against the PermissionRule methods
 * that are available.
 *
 * Subclasses of this base class are therefore able to test the security annotations of their various methods by verifying
 * that individual PermissionRule methods are being called (on their owning mocks)
 */
public abstract class BaseServiceSecurityTest<T> extends BaseIntegrationTest {

    @Autowired
    private GenericApplicationContext applicationContext;

    protected T service;

    private Map<Class<?>, Object> mockPermissionRulesBeans;

    private Map<Class<?>, Map<String, List<Pair<Object, Method>>>> originalRulesMap;

    /**
     * @return the service class under test.  Note that in order for Spring Security to be able to read parameter-name
     * information from expressions like @PreAuthorize("hasPermission(#feedback, 'UPDATE')"), we cannot provide a
     * Mockito mock of type T, as Spring Security is unable to infer which parameter is called "feedback", in this example.
     *
     * Therefore we need to just create a very simple implementation of T.
     */
    protected abstract Class<? extends T> getServiceClass();

    /**
     * Look up a Mockito mock for a given PermissionRules-annotated bean class
     *
     * @param clazz
     * @param <T>
     * @return
     */
    protected <T> T getMockPermissionRulesBean(Class<T> clazz) {
        return (T) mockPermissionRulesBeans.get(clazz);
    }

    /**
     * Register a temporary bean definition for the class under test (as provided by getServiceClass()), and replace
     * all PermissionRules with mocks that can be looked up with getMockPermissionRulesBean().
     */
    @Before
    public void setup() {

        applicationContext.registerBeanDefinition("beanUndergoingSecurityTesting", new RootBeanDefinition(getServiceClass()));
        service = (T) applicationContext.getBean("beanUndergoingSecurityTesting");

        // Process mock annotations
        MockitoAnnotations.initMocks(this);

        // get the custom permission evaluator from the applicationContext and swap its rulesMap for one containing only
        // Mockito mocks
        CustomPermissionEvaluator permissionEvaluator = (CustomPermissionEvaluator) applicationContext.getBean("customPermissionEvaluator");

        originalRulesMap =
                (Map<Class<?>, Map<String, List<Pair<Object, Method>>>>) getField(permissionEvaluator, "rulesMap");

        Pair<Map<Class<?>, Object>, Map<Class<?>, Map<String, List<Pair<Object, Method>>>>> mockedOut = generateMockedOutRulesMap(originalRulesMap);

        mockPermissionRulesBeans = mockedOut.getLeft();
        Map<Class<?>, Map<String, List<Pair<Object, Method>>>> newMockRulesMap = mockedOut.getRight();

        setField(permissionEvaluator, "rulesMap", newMockRulesMap);

        setLoggedInUser(newUser().build());
    }

    /**
     * Revert the temporary bean definintions used for testing, and replace the original rulesMap on the custom permission evaluator
     */
    @After
    public void teardown() {
        applicationContext.removeBeanDefinition("beanUndergoingSecurityTesting");
        CustomPermissionEvaluator permissionEvaluator = (CustomPermissionEvaluator) applicationContext.getBean("customPermissionEvaluator");
        setField(permissionEvaluator, "rulesMap", originalRulesMap);
    }

    /**
     * Set a user on the Spring Security ThreadLocals
     *
     * @param user
     */
    protected void setLoggedInUser(User user) {
        SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(user));
    }

    /**
     * Given the rulesMap from the CustomPermissionEvaluator, this method replaces all of the original @PermissionRules-annotated beans
     * with Mockito mocks, and all of the @PermissionRule-annotated methods on those beans with the equivalent methods from the mocks.
     *
     * This method then returns all of the mocks that it has created (so for each original @PermissionRules class, there will be an
     * equivalent mock) and the new rulesMap containing the mock replacements.
     *
     * @param originalRulesMap
     * @return
     */
    private Pair<Map<Class<?>, Object>, Map<Class<?>, Map<String, List<Pair<Object, Method>>>>> generateMockedOutRulesMap(
            Map<Class<?>, Map<String, List<Pair<Object, Method>>>> originalRulesMap) {

        Map<Class<?>, Object> mockPermissionRulesBeans = new HashMap<>();

        Map<Class<?>, Map<String, List<Pair<Object, Method>>>> newMockRulesMap = new HashMap<>();

        for (Map.Entry<Class<?>, Map<String, List<Pair<Object, Method>>>> entry : originalRulesMap.entrySet()) {

            Class<?> originalDtoClass = entry.getKey();
            Map<String, List<Pair<Object, Method>>> originalPermissionBeansAndMethodsByPermission = entry.getValue();

            Map<String, List<Pair<Object, Method>>> newMockPermissionBeansAndMethodsByPermission = new HashMap<>();

            for (Map.Entry<String, List<Pair<Object, Method>>> originalPermissionBeansAndMethods : originalPermissionBeansAndMethodsByPermission.entrySet()) {

                String originalPermission = originalPermissionBeansAndMethods.getKey();
                List<Pair<Object, Method>> originalListOfPermissionMethods = originalPermissionBeansAndMethods.getValue();
                List<Pair<Object, Method>> newMockListOfPermissionMethods = new ArrayList<>();

                for (Pair<Object, Method> beanAndPermissionMethods : originalListOfPermissionMethods) {

                    Object originalPermissionRulesBean = beanAndPermissionMethods.getKey();

                    if (!mockPermissionRulesBeans.containsKey(originalPermissionRulesBean.getClass())) {
                        mockPermissionRulesBeans.put(originalPermissionRulesBean.getClass(), mock(originalPermissionRulesBean.getClass()));
                    }

                    final Object mockPermissionsRulesBean = mockPermissionRulesBeans.get(originalPermissionRulesBean.getClass());
                    Method originalPermissionMethod = beanAndPermissionMethods.getRight();
                    String methodName = originalPermissionMethod.getName();
                    Class<?>[] methodParameters = originalPermissionMethod.getParameterTypes();
                    final Method mockPermissionMethod;

                    try {
                        mockPermissionMethod = mockPermissionsRulesBean.getClass().getMethod(methodName, methodParameters);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException("Unable to look up same method on mock", e);
                    }

                    newMockListOfPermissionMethods.add(Pair.of(mockPermissionsRulesBean, mockPermissionMethod));
                }

                newMockPermissionBeansAndMethodsByPermission.put(originalPermission, newMockListOfPermissionMethods);
            }

            newMockRulesMap.put(originalDtoClass, newMockPermissionBeansAndMethodsByPermission);
        }

        return Pair.of(mockPermissionRulesBeans, newMockRulesMap);
    }
}
