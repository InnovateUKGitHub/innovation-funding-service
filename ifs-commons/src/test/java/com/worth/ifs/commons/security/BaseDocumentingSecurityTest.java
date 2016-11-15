package com.worth.ifs.commons.security;

import au.com.bytecode.opencsv.CSVWriter;
import com.worth.ifs.commons.BaseIntegrationTest;
import com.worth.ifs.user.resource.UserRoleType;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.Before;
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
import java.util.function.Predicate;

import static com.worth.ifs.user.builder.RoleResourceBuilder.newRoleResource;
import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;
import static com.worth.ifs.util.CollectionFunctions.simpleJoiner;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.EnumSet.complementOf;
import static java.util.EnumSet.copyOf;
import static org.junit.Assert.fail;
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

        applicationContext.registerBeanDefinition("beanUndergoingSecurityTesting", new RootBeanDefinition(getClassUnderTest()));

        T serviceBeanWithSpringSecurity = (T) applicationContext.getBean("beanUndergoingSecurityTesting");

        classUnderTest = createRecordingProxy(serviceBeanWithSpringSecurity, getClassUnderTest(),
                method -> hasOneAnnotation(method, PreAuthorize.class, PostAuthorize.class, PreFilter.class, PostFilter.class),
                methodCalled -> recordServiceMethodCall(methodCalled)
        );

        super.setup();
    }

    private void recordPermissionRuleMethodCall(Method methodCalled, Class<?> permissionRuleClass) {
        recordedRuleInteractions.add(Pair.of(RecordingSource.PERMISSION_RULE, permissionRuleClass.getSimpleName() + "." + methodCalled.getName()));
    }

    private void recordServiceMethodCall(Method methodCalled) {

        Class<?>[] interfaces = getClassUnderTest().getInterfaces();
        Class<?> serviceInterface = interfaces.length > 0 ? interfaces[0] : getClassUnderTest();
        recordedRuleInteractions.add(Pair.of(RecordingSource.SERVICE, serviceInterface.getSimpleName() + "." + methodCalled.getName()));

        SecuredBySpring simpleSecuredAnnotation = AnnotationUtils.findAnnotation(methodCalled, SecuredBySpring.class);
        if (simpleSecuredAnnotation != null) {
            recordedRuleInteractions.add(Pair.of(RecordingSource.PERMISSION_RULE, serviceInterface.getSimpleName() + "." + methodCalled.getName()));
        }
    }

    /**
     * Replace the original rulesMap and lookup strategy on the custom permission evaluator.
     *
     * Additionally, revert the temporary bean definition for the Service under test
     */
    @After
    public void teardown() {
        applicationContext.removeBeanDefinition("beanUndergoingSecurityTesting");

        documentServiceAndPermissionRuleInteractions();

        super.teardown();
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
        return createRecordingProxy(mock, mockClass,
                method -> hasOneAnnotation(method, PermissionRule.class),
                methodCalled -> recordPermissionRuleMethodCall(methodCalled, mockClass)
        );
    }

    /**
     * Create a pass-through proxy that is able to record interactions with mock objects in a similar way that Mockito does
     * (Mockito does not expose its recordings and so it is necessary to do this manually if you want a list of interactions available)
     *
     * @param instance
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T createRecordingProxy(Object instance, Class<T> clazz, Predicate<Method> proxyMethodFilter, Consumer<Method> methodCallHandler) {

        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(clazz);
        factory.setFilter(proxyMethodFilter::test);

        MethodHandler handler = (self, thisMethod, proceed, args) -> {
            Method originalMethod = instance.getClass().getMethod(thisMethod.getName(), thisMethod.getParameterTypes());
            methodCallHandler.accept(originalMethod);
            try {
                return originalMethod.invoke(instance, args);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        };

        try {
            return (T) factory.create(new Class<?>[0], new Object[0], handler);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean hasOneAnnotation(Method method, Class<? extends Annotation>... annotations) {
        return asList(annotations).stream().anyMatch(annotation -> findAnnotation(method, annotation) != null);
    }

    protected final void testOnlyAUserWithOneOfTheGlobalRolesCan(Runnable functionToCall, UserRoleType... roles){
        EnumSet<UserRoleType> rolesThatShouldSucceed = EnumSet.copyOf(asList(roles));
        EnumSet<UserRoleType> rolesThatShouldFail = complementOf(rolesThatShouldSucceed);
        rolesThatShouldFail.forEach(role -> {
            BaseIntegrationTest.setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(role).build())).build());
            try {
                functionToCall.run();
                fail("Should not have been able to run the function given the role: " + role);
            } catch (AccessDeniedException e) {
                // expected behaviour
            }
        });
        rolesThatShouldSucceed.forEach(role -> {
            BaseIntegrationTest.setLoggedInUser(newUserResource().withRolesGlobal(singletonList(newRoleResource().withType(role).build())).build());
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
