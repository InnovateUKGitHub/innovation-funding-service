package org.innovateuk.ifs;

import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.security.SecuredMethodsInStackCountInterceptor;
import org.innovateuk.ifs.security.NotSecuredMethodException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.security.access.prepost.PostFilter;

import java.util.List;
import java.util.concurrent.*;

import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;


/**
 * Tests to check that the {@link NotSecured#mustBeSecuredByOtherServices()} property will be obeyed.
 */
public class NotSecuredSecurityTest extends BaseIntegrationTest {

    @Autowired
    private SecuredMethodsInStackCountInterceptor securedMethodsInStackCountInterceptor;

    @Autowired
    private GenericApplicationContext applicationContext;
    private TestServiceLevel1 springWrappedTestServiceLevel1;
    private TestServiceLevel2 springWrappedTestServiceLevel2;
    private TestServiceLevel3 springWrappedTestServiceLevel3;
    private TestServiceCrossThread springWrappedTestServiceCrossThread;

    private ITestServiceLevel1 springWrappedTestServiceLevel1Impl;
    private ITestServiceLevel2 springWrappedTestServiceLevel2Impl;
    private ITestServiceLevel3 springWrappedTestServiceLevel3Impl;

    @Before
    public void setup() {

        applicationContext.registerBeanDefinition("springWrappedTestServiceLevel1", new RootBeanDefinition(TestServiceLevel1.class));
        springWrappedTestServiceLevel1 = (TestServiceLevel1) applicationContext.getBean("springWrappedTestServiceLevel1");
        applicationContext.registerBeanDefinition("springWrappedTestServiceLevel2", new RootBeanDefinition(TestServiceLevel2.class));
        springWrappedTestServiceLevel2 = (TestServiceLevel2) applicationContext.getBean("springWrappedTestServiceLevel2");
        applicationContext.registerBeanDefinition("springWrappedTestServiceLevel3", new RootBeanDefinition(TestServiceLevel3.class));
        springWrappedTestServiceLevel3 = (TestServiceLevel3) applicationContext.getBean("springWrappedTestServiceLevel3");

        applicationContext.registerBeanDefinition("springWrappedTestServiceLevel1Impl", new RootBeanDefinition(TestServiceLevel1Impl.class));
        springWrappedTestServiceLevel1Impl = (ITestServiceLevel1) applicationContext.getBean("springWrappedTestServiceLevel1Impl");
        applicationContext.registerBeanDefinition("springWrappedTestServiceLevel2Impl", new RootBeanDefinition(TestServiceLevel2Impl.class));
        springWrappedTestServiceLevel2Impl = (ITestServiceLevel2) applicationContext.getBean("springWrappedTestServiceLevel2Impl");
        applicationContext.registerBeanDefinition("springWrappedTestServiceLevel3Impl", new RootBeanDefinition(TestServiceLevel3Impl.class));
        springWrappedTestServiceLevel3Impl = (ITestServiceLevel3) applicationContext.getBean("springWrappedTestServiceLevel3Impl");

        setLoggedInUser(newUserResource().build());
    }

    @Test
    public void testNotSecuredMethodsAreAllowedWhenMustBeSecuredByOtherServicesIsFalse() {
        springWrappedTestServiceLevel1.notSecuredAndDoesNotNeedToBe(); // Should not throw
    }

    @Test(expected = NotSecuredMethodException.class)
    public void testNotSecuredMethodsAreNotAllowedWhenMustBeSecuredByOtherServicesIsTrue() {
        springWrappedTestServiceLevel1.notSecuredButNeedsToBeSecuredByAMethodHigherOnTheStack();

    }

    @Test
    public void testNotSecuredMethodsAreAllowedWhenMustBeSecuredByOtherServicesIsTrueButThereIsASecuredMethodAboveIt() {
        springWrappedTestServiceLevel2.aSecuredMethodThatCallsAnUnsecuredMethodThatNeedsToBeSecured(); // Should not throw
    }


    @Test(expected = NotSecuredMethodException.class)
    public void testNotSecuredMethodsAreNotAllowedWhenMustBeSecuredByOtherServicesIsTrueAndThereIsNotASecuredMethodAboveIt() {
        springWrappedTestServiceLevel2.aNotSecuredMethodThatCallsAnUnsecuredMethodThatNeedsToBeSecured();
    }

    @Test
    public void testWhenThereAreAFewMethodsInTheStack() {
        springWrappedTestServiceLevel3.aSecuredMethodThatCallsASecuredMethodThatCallsAnUnsecuredMethodThatNeedsToBeSecured(); // Should not throw.
    }

    @Test(expected = NotSecuredMethodException.class)
    public void testWhenCallingThereIsAMethodThatIsOkayAndThenOneThatIsNot() {
        springWrappedTestServiceLevel1.notSecuredButNeedsToBeSecuredByAMethodHigherOnTheStack();
    }

    @Test
    public void testThatWeDoNotBleedAcrossThreads() throws Exception {
        applicationContext.registerBeanDefinition("springWrappedTestServiceCrossThread", new RootBeanDefinition(TestServiceCrossThread.class));
        springWrappedTestServiceCrossThread = (TestServiceCrossThread) applicationContext.getBean("springWrappedTestServiceCrossThread");
        final ExecutorService executorService = Executors.newFixedThreadPool(2);
        // Call the blocking secured method
        Future<List<String>> blocked = executorService.submit(() -> {
            setLoggedInUser(newUserResource().build());
            return springWrappedTestServiceCrossThread.aSecuredMethodThatBlocks();
        });
        // Await until we are sure we are in the blocking method
        executorService.submit(() -> springWrappedTestServiceCrossThread.awaitUntilBlockMethodEntered()).get();
        // Call the unsecured method now we are sure we are blocked in a secured method in a different thread.
        final Future<List<String>> shouldThrow = executorService.submit(() -> springWrappedTestServiceCrossThread.notSecuredButNeedsToBeSecuredByAMethodHigherOnTheStack());
        try {
            shouldThrow.get();
        } catch (final ExecutionException e) {
            Assert.assertTrue(e.getCause() instanceof NotSecuredMethodException);
        }
        // Finally cancel the blocked task.
        blocked.cancel(true);
    }

    @Test
    public void testExceptions() throws Exception {
        Assert.assertFalse(securedMethodsInStackCountInterceptor.isStackSecured());
        try {
            springWrappedTestServiceLevel1.aSecuredMethodThatThrowsAnException();
        } catch (Exception e) {
            // Expected but check we are not still secured.
            Assert.assertFalse(securedMethodsInStackCountInterceptor.isStackSecured());
        }
    }

    @Test
    public void testNotSecuredMethodsAreAllowedWhenMustBeSecuredByOtherServicesIsFalseImpl() {
        springWrappedTestServiceLevel1Impl.notSecuredAndDoesNotNeedToBe(); // Should not throw
    }

    @Test(expected = NotSecuredMethodException.class)
    public void testNotSecuredMethodsAreNotAllowedWhenMustBeSecuredByOtherServicesIsTrueImpl() {
        springWrappedTestServiceLevel1Impl.notSecuredButNeedsToBeSecuredByAMethodHigherOnTheStack();

    }

    @Test
    public void testNotSecuredMethodsAreAllowedWhenMustBeSecuredByOtherServicesIsTrueButThereIsASecuredMethodAboveItImpl() {
        springWrappedTestServiceLevel2Impl.aSecuredMethodThatCallsAnUnsecuredMethodThatNeedsToBeSecured(); // Should not throw
    }


    @Test(expected = NotSecuredMethodException.class)
    public void testNotSecuredMethodsAreNotAllowedWhenMustBeSecuredByOtherServicesIsTrueAndThereIsNotASecuredMethodAboveItImpl() {
        springWrappedTestServiceLevel2Impl.aNotSecuredMethodThatCallsAnUnsecuredMethodThatNeedsToBeSecured();
    }

    @Test
    public void testWhenThereAreAFewMethodsInTheStackImpl() {
        springWrappedTestServiceLevel3Impl.aSecuredMethodThatCallsASecuredMethodThatCallsAnUnsecuredMethodThatNeedsToBeSecured(); // Should not throw.
    }

    @Test(expected = NotSecuredMethodException.class)
    public void testWhenCallingThereIsAMethodThatIsOkayAndThenOneThatIsNotImpl() {
        springWrappedTestServiceLevel1Impl.notSecuredButNeedsToBeSecuredByAMethodHigherOnTheStack();
    }

    @Test
    public void testExceptionsImpl() throws Exception {
        Assert.assertFalse(securedMethodsInStackCountInterceptor.isStackSecured());
        try {
            springWrappedTestServiceLevel1Impl.aSecuredMethodThatThrowsAnException();
        } catch (Exception e) {
            // Expected but check we are not still secured.
            Assert.assertFalse(securedMethodsInStackCountInterceptor.isStackSecured());
        }
    }

    interface ITestServiceLevel1 {

        @NotSecured(value = "This method needs no securing", mustBeSecuredByOtherServices = false)
        void notSecuredAndDoesNotNeedToBe();

        @NotSecured(value = "This method need securing by a method higher on the stack", mustBeSecuredByOtherServices = true)
        void notSecuredButNeedsToBeSecuredByAMethodHigherOnTheStack();

        @PostFilter("hasPermission(filterObject, 'READ')")
        void aSecuredMethodThatThrowsAnException();
    }

    public static class TestServiceLevel1 {

        @NotSecured(value = "This method needs no securing", mustBeSecuredByOtherServices = false)
        public void notSecuredAndDoesNotNeedToBe() {
        }

        @NotSecured(value = "This method need securing by a method higher on the stack", mustBeSecuredByOtherServices = true)
        public void notSecuredButNeedsToBeSecuredByAMethodHigherOnTheStack() {
        }

        @PostFilter("hasPermission(filterObject, 'READ')")
        public void aSecuredMethodThatThrowsAnException() {
            throw new RuntimeException();
        }


    }


    public static class TestServiceLevel1Impl implements ITestServiceLevel1 {

        @Override
        public void notSecuredAndDoesNotNeedToBe() {
        }

        @Override
        public void notSecuredButNeedsToBeSecuredByAMethodHigherOnTheStack() {
        }

        @Override
        public void aSecuredMethodThatThrowsAnException() {
            throw new RuntimeException();
        }


    }

    interface ITestServiceLevel2 {

        @PostFilter("hasPermission('filterObject', READ)")
        List<String> aSecuredMethodThatCallsAnUnsecuredMethodThatNeedsToBeSecured();

        List<String> aNotSecuredMethodThatCallsAnUnsecuredMethodThatNeedsToBeSecured();
    }

    public static class TestServiceLevel2 {

        @Autowired
        private TestServiceLevel1 testServiceLevel1;

        @PostFilter("hasPermission('filterObject', READ)")
        public List<String> aSecuredMethodThatCallsAnUnsecuredMethodThatNeedsToBeSecured() {
            testServiceLevel1.notSecuredButNeedsToBeSecuredByAMethodHigherOnTheStack();
            return null;
        }

        public List<String> aNotSecuredMethodThatCallsAnUnsecuredMethodThatNeedsToBeSecured() {
            testServiceLevel1.notSecuredButNeedsToBeSecuredByAMethodHigherOnTheStack();
            return null;
        }
    }

    public static class TestServiceLevel2Impl implements ITestServiceLevel2 {

        @Autowired
        private TestServiceLevel1 testServiceLevel1;

        @Override
        public List<String> aSecuredMethodThatCallsAnUnsecuredMethodThatNeedsToBeSecured() {
            testServiceLevel1.notSecuredButNeedsToBeSecuredByAMethodHigherOnTheStack();
            return null;
        }

        @Override
        public List<String> aNotSecuredMethodThatCallsAnUnsecuredMethodThatNeedsToBeSecured() {
            testServiceLevel1.notSecuredButNeedsToBeSecuredByAMethodHigherOnTheStack();
            return null;
        }
    }

    interface ITestServiceLevel3 {

        @PostFilter("hasPermission('filterObject', READ)")
        List<String> aSecuredMethodThatCallsASecuredMethodThatCallsAnUnsecuredMethodThatNeedsToBeSecured();
    }

    public static class TestServiceLevel3 {

        @Autowired
        private TestServiceLevel2 testServiceLevel2;

        @PostFilter("hasPermission('filterObject', READ)")
        public List<String> aSecuredMethodThatCallsASecuredMethodThatCallsAnUnsecuredMethodThatNeedsToBeSecured() {
            testServiceLevel2.aSecuredMethodThatCallsAnUnsecuredMethodThatNeedsToBeSecured();
            return null;
        }
    }

    public static class TestServiceLevel3Impl implements ITestServiceLevel3 {

        @Autowired
        private TestServiceLevel2 testServiceLevel2;

        @Override
        public List<String> aSecuredMethodThatCallsASecuredMethodThatCallsAnUnsecuredMethodThatNeedsToBeSecured() {
            testServiceLevel2.aSecuredMethodThatCallsAnUnsecuredMethodThatNeedsToBeSecured();
            return null;
        }
    }

    public static class TestServiceCrossThread {

        public final CountDownLatch arrivedInBlockingMethod = new CountDownLatch(1);
        public final CountDownLatch blockInBlockingMethod = new CountDownLatch(1);

        @NotSecured(value = "This method need securing by a method higher on the stack", mustBeSecuredByOtherServices = true)
        public List<String> notSecuredButNeedsToBeSecuredByAMethodHigherOnTheStack() {
            return null;
        }

        public Boolean awaitUntilBlockMethodEntered() throws Exception {
            arrivedInBlockingMethod.await();
            return true;
        }

        @PostFilter("hasPermission('filterObject', READ)")
        public List<String> aSecuredMethodThatBlocks() throws Exception {
            arrivedInBlockingMethod.countDown(); // We have arrived in the blocking method.
            blockInBlockingMethod.await(); // Block
            return null;
        }
    }
}
