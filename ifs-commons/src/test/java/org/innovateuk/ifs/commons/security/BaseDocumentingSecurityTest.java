package org.innovateuk.ifs.commons.security;

import au.com.bytecode.opencsv.CSVWriter;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.attribute.MethodAttributeAppender;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.apache.commons.lang3.tuple.Pair;
import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.innovateuk.ifs.commons.ProxyUtils;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.After;
import org.junit.Before;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.EnumSet.complementOf;
import static net.bytebuddy.matcher.ElementMatchers.*;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleJoiner;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

/**
 * A base class for testing services with Spring Security integrated into them.  PermissionRules-annotated beans are
 * made available as mocks so that we can test the effects of calling service methods against the PermissionRule methods
 * that are available.
 *
 * Calls for Service methods and the associated Permission Rule methods that are called as a result are recorded and output
 * to a CSV report as a standard part of the testing process.
 *
 * Subclasses of this base class are therefore able to test the security annotations of their various methods by verifying
 * that individual PermissionRule methods are being called (on their owning mocks) and the same verifications auto-documented
 */
public abstract class BaseDocumentingSecurityTest<T> extends BaseMockSecurityTest {

    protected T classUnderTest;

    /**
     * The underlying mock of the class that is currently
     * being security tested. Expectations can be set
     * on this like you would normally e.g.
     * {@code when(classUnderTestMock.doSomething()).thenReturn();}
     */
    protected T classUnderTestMock;

    /**
     * As developers might still be using the old security
     * testing pattern of defining a stub implementation,
     * we should acquire the 'true' intended target class
     * that we will be recording interactions with.
     */
    private Class<T> targetClass;

    /**
     * Service calls and their associated Permission Rules calls are recorded as they occur.  This enum allows us to
     * determine which type of originator (service or permission rule) the method call was from when it was recorded.
     */
    private enum RecordingSource {
        SERVICE,
        PERMISSION_RULE
    }

    /**
     * This list contains a list of method calls being recorded in the order that they occur, from both services and their
     * subsequent permission rule calls
     */
    private List<Pair<RecordingSource, String>> recordedRuleInteractions = new ArrayList<>();

    /**
     * @return the service class under test.  Note that in order for Spring Security to be able to read parameter-name
     * information from expressions like @PreAuthorize("hasPermission(#feedback, 'UPDATE')"), we cannot provide a
     * Mockito mock of type T, as Spring Security is unable to infer which parameter is called "feedback", in this example.
     * <p>
     * Therefore we need to just create a very simple implementation of T.
     */
    protected abstract Class<? extends T> getClassUnderTest();

    protected abstract String getDocumentationFilename();

    /**
     * Register a temporary bean definition for the Service under test (as provided by getClassUnderTest()), and replace
     * all PermissionRules with mocks that can be looked up with getMockPermissionRulesBean().
     *
     * Additionally wrap the service in a proxy that is able to record method invocations on secured methods, in order
     * to then marry up the service call to any subsequent permission rule method invocations
     */
    @Before
    public void setup() {
        Class<?>[] interfaces = getClassUnderTest().getInterfaces();
        targetClass = (Class<T>) (interfaces.length >= 1 ? interfaces[0] : getClassUnderTest());

        classUnderTestMock = createDelegatingProxy(targetClass, mock(targetClass));

        Object securedSpringProxy = ProxyUtils.createProxy(
                targetClass,
                classUnderTestMock,
                getAdvisedSecuredBean().getAdvisors()
        );

        classUnderTest = createRecordingProxy(
                targetClass,
                securedSpringProxy,
                method -> {
                    if (hasOneAnnotation(method, PreAuthorize.class, PostAuthorize.class, PreFilter.class, PostFilter.class)) {
                        recordServiceMethodCall(method);
                    }
                }
        );

        super.setup();
    }

    private Advised getAdvisedSecuredBean() {
        if (applicationContext.getBeanNamesForType(targetClass).length >= 1) {
            return (Advised) applicationContext.getBean(targetClass);
        } else {
            applicationContext.registerBeanDefinition(
                    "beanUndergoingSecurityTesting",
                    new RootBeanDefinition(targetClass)
            );

            return (Advised) applicationContext.getBean("beanUndergoingSecurityTesting");
        }
    }

    private static <U> U createDelegatingProxy(Class<U> targetClass, Object targetInstance) {
        try {
            return new ByteBuddy()
                    .subclass(targetClass)
                    .method(isPublic().and(not(isDeclaredBy(Object.class))))
                    .intercept(MethodDelegation.to(targetInstance))
                    .attribute(MethodAttributeAppender.ForInstrumentedMethod.INCLUDING_RECEIVER)
                    .make()
                    .load(targetClass.getClassLoader())
                    .getLoaded()
                    .newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Could not delegate method calls to target class through target instance.", e);
        }
    }

    private void recordPermissionRuleMethodCall(Method methodCalled, Class<?> permissionRuleClass) {
        recordedRuleInteractions.add(Pair.of(
                RecordingSource.PERMISSION_RULE,
                permissionRuleClass.getSimpleName() + "." + methodCalled.getName()
        ));
    }

    private void recordServiceMethodCall(Method methodCalled) {
        recordedRuleInteractions.add(Pair.of(
                RecordingSource.SERVICE,
                targetClass.getSimpleName() + "." + methodCalled.getName()
        ));

        SecuredBySpring simpleSecuredAnnotation = AnnotationUtils.findAnnotation(methodCalled, SecuredBySpring.class);
        if (simpleSecuredAnnotation != null) {
            recordedRuleInteractions.add(Pair.of(
                    RecordingSource.PERMISSION_RULE,
                    targetClass.getSimpleName() + "." + methodCalled.getName()
            ));
        }
    }

    /**
     * Replace the original rulesMap and lookup strategy on the custom permission evaluator.
     *
     * Additionally, revert the temporary bean definition for the Service under test
     */
    @After
    public void unregisterBeanAndDocument() {
        if (applicationContext.isBeanNameInUse("beanUndergoingSecurityTesting")) {
            applicationContext.removeBeanDefinition("beanUndergoingSecurityTesting");
        }

        documentServiceAndPermissionRuleInteractions();
    }

    /**
     * Append the latest set of recorded service-to-permission-rule method calls to the csv report, collating
     * individual permission rule invocations against the service method invocation that caused them to be called
     */
    private void documentServiceAndPermissionRuleInteractions() {

        List<Pair<String, Set<String>>> serviceMethodsToPermissionRuleRecordings = new ArrayList<>();

        recordedRuleInteractions.forEach(recordedMethodCall -> {

            RecordingSource sourceOfRecording = recordedMethodCall.getLeft();

            if (sourceOfRecording == RecordingSource.SERVICE) {
                serviceMethodsToPermissionRuleRecordings.add(Pair.of(recordedMethodCall.getRight(), new LinkedHashSet<>()));
            } else {
                Pair<String, Set<String>> latestServiceCallRecordings =
                        serviceMethodsToPermissionRuleRecordings.get(serviceMethodsToPermissionRuleRecordings.size() - 1);

                latestServiceCallRecordings.getRight().add(recordedMethodCall.getRight());
            }
        });

        writeRecordingsToCsv(serviceMethodsToPermissionRuleRecordings);
    }

    /**
     * Given a list of service methods and the various permission rule(s) that are invoked as a result of each service method
     * being called, append this information to the CSV report
     *
     * @param serviceMethodsToPermissionRuleRecordings
     */
    private void writeRecordingsToCsv(List<Pair<String, Set<String>>> serviceMethodsToPermissionRuleRecordings) {

        try (FileWriter fileWriter = new FileWriter(getDocumentationFilename(), true)) {

            CSVWriter writer = new CSVWriter(fileWriter, '\t');

            serviceMethodsToPermissionRuleRecordings.forEach(recording -> {

                String serviceMethod = recording.getLeft();
                Set<String> permissionRuleCalls = recording.getRight();
                String permissionRuleCallsCombined = simpleJoiner(new ArrayList<>(permissionRuleCalls), "\n");

                writer.writeNext(new String[]{serviceMethod, permissionRuleCallsCombined});
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            recordedRuleInteractions.clear();
        }
    }

    /**
     * Wrap any @PermissionRules mocks with a proxy that records their method invocations, in order to then marry up the
     * permission rule method invocation with the last recorded service call
     *
     * @param mock
     * @param mockClass
     * @return
     */
    @Override
    protected Object createPermissionRuleMock(Object mock, Class<?> mockClass) {
        return createRecordingProxy(
                mockClass,
                mock,
                method -> {
                    if (hasOneAnnotation(method, PermissionRule.class)) {
                        recordPermissionRuleMethodCall(method, mockClass);
                    }
                }
        );
    }

    /**
     * Interceptor that catches any matching methods
     * (according to criteria set by the ByteBuddy dynamic subclass)
     * and delegates their invocation to a target instance.
     *
     * It also accepts a method handler that allows some logic to be
     * performed before the method is invoked on the target instance.
     */
    public static class DelegatingMethodInterceptor {

        private Object target;
        private Consumer<Method> handler;

        public DelegatingMethodInterceptor(Object target, Consumer<Method> handler) {
            this.target = target;
            this.handler = handler;
        }

        @RuntimeType
        public Object intercept(@Origin Method method, @AllArguments Object... args) throws Throwable {
            handler.accept(method);

            try {
                return method.invoke(target, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw e.getCause();
            }
        }
    }

    private static <U> U createRecordingProxy(Class<U> targetClass, Object targetInstance, Consumer<Method> methodHandler) {
        DelegatingMethodInterceptor interceptor = new DelegatingMethodInterceptor(targetInstance, methodHandler);

        return createDelegatingProxy(targetClass, interceptor);
    }

    private boolean hasOneAnnotation(Method method, Class<? extends Annotation>... annotations) {
        return asList(annotations).stream().anyMatch(annotation -> findAnnotation(method, annotation) != null);
    }

    protected final void testOnlyAUserWithOneOfTheGlobalRolesCan(Runnable functionToCall, Role... roles){
        EnumSet<Role> rolesThatShouldSucceed = EnumSet.copyOf(asList(roles));
        EnumSet<Role> rolesThatShouldFail = complementOf(rolesThatShouldSucceed);
        rolesThatShouldFail.forEach(role -> {
            BaseIntegrationTest.setLoggedInUser(newUserResource().withRolesGlobal(singletonList(role)).build());
            try {
                functionToCall.run();
                fail("Should not have been able to run the function given the role: " + role);
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
        // No roles should also fail
        BaseIntegrationTest.setLoggedInUser(newUserResource().build());
        try {
            functionToCall.run();
            fail("Should not have been able to run the function given no role");
        } catch (AccessDeniedException e) {
            // expected behaviour
        }
        rolesThatShouldSucceed.forEach(role -> {
            BaseIntegrationTest.setLoggedInUser(newUserResource().withRolesGlobal(singletonList(Role.getByName(role.getName()))).build());
            try {
                functionToCall.run();
                // Should not throw
            }
            catch (AccessDeniedException e){
                fail("Should have been able to run the function given the role: " + role);
            }
        });
    }
}
