package com.worth.ifs;

import com.worth.ifs.commons.BaseIntegrationTest;
import com.worth.ifs.commons.security.NotSecured;
import com.worth.ifs.security.NotSecuredMethodException;
import com.worth.ifs.security.SecuredMethodsInStackCountInterceptor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.security.access.prepost.PostFilter;

import java.util.List;
import java.util.concurrent.*;

import static com.worth.ifs.user.builder.UserResourceBuilder.newUserResource;


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

    @Before
    public void setup() {
        applicationContext.registerBeanDefinition("springWrappedTestServiceLevel1", new RootBeanDefinition(TestServiceLevel1.class));
        springWrappedTestServiceLevel1 = (TestServiceLevel1) applicationContext.getBean("springWrappedTestServiceLevel1");
        applicationContext.registerBeanDefinition("springWrappedTestServiceLevel2", new RootBeanDefinition(TestServiceLevel2.class));
        springWrappedTestServiceLevel2 = (TestServiceLevel2) applicationContext.getBean("springWrappedTestServiceLevel2");
        applicationContext.registerBeanDefinition("springWrappedTestServiceLevel3", new RootBeanDefinition(TestServiceLevel3.class));
        springWrappedTestServiceLevel3 = (TestServiceLevel3) applicationContext.getBean("springWrappedTestServiceLevel3");
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

    public static class TestServiceLevel3 {

        @Autowired
        private TestServiceLevel2 testServiceLevel2;

        @PostFilter("hasPermission('filterObject', READ)")
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
