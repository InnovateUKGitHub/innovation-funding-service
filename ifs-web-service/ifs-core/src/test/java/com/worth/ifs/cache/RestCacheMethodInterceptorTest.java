package com.worth.ifs.cache;


import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RestCacheMethodInterceptorTest {

    @Test
    public void testResultsCached() {
        // First the actual base class
        final TestClassImpl base = new TestClassImpl();
        // Now the caching layer proxy
        final InvocationHandler handler = forBaseWithInterceptor(base, new RestCacheMethodInterceptor().setUidSupplier(() -> "1"));
        TestClass proxy = (TestClass) Proxy.newProxyInstance(TestClass.class.getClassLoader(), new Class[]{TestClass.class}, handler);
        // Call method1 repeatedly
        proxy.method1();
        proxy.method1();
        proxy.method1();
        Assert.assertEquals(1, base.numberTimesMethod1Called);
        // Call method2 repeatedly with the same args
        proxy.method2(1);
        proxy.method2(1);
        proxy.method2(1);
        Assert.assertEquals(1, base.numberTimesMethod2Called);
        // Call method2 again with different args and check that the number of calls goes up by 1
        proxy.method2(2);
        Assert.assertEquals(2, base.numberTimesMethod2Called);
        // Call method2 again with the first arg sent and check that we are still cached
        proxy.method2(1);
        Assert.assertEquals(2, base.numberTimesMethod2Called);
    }

    @Test
    public void testResultsCachedPerUid() {
        // First the actual base class
        final TestClassImpl base = new TestClassImpl();
        // Now the caching layer proxy
        final UidSupplierImpl uidSupplier = new UidSupplierImpl().setUid("a");
        final InvocationHandler handler = forBaseWithInterceptor(base, new RestCacheMethodInterceptor().setUidSupplier(uidSupplier));
        final TestClass proxy = (TestClass) Proxy.newProxyInstance(TestClass.class.getClassLoader(), new Class[]{TestClass.class}, handler);
        // Call method1 repeatedly and check the base is only called once.
        proxy.method1();
        proxy.method1();
        proxy.method1();
        Assert.assertEquals(1, base.numberTimesMethod1Called);
        // Now use a different uid and call method1 again and check that the base is called again, but only one more time
        uidSupplier.setUid("b");
        uidSupplier.setUid("b");
        uidSupplier.setUid("b");
        proxy.method1();
        Assert.assertEquals(2, base.numberTimesMethod1Called);
    }

    @Test
    public void testResultsInvalidated() {
        // First the actual base class
        final TestClassImpl base = new TestClassImpl();
        // Now the caching layer proxy
        final UidSupplierImpl uidSupplier = new UidSupplierImpl().setUid("a");
        final RestCacheMethodInterceptor cachingInterceptor = new RestCacheMethodInterceptor().setUidSupplier(uidSupplier);
        final InvocationHandler cachingHandler = forBaseWithInterceptor(base, cachingInterceptor);
        final TestClass proxy = (TestClass) Proxy.newProxyInstance(TestClass.class.getClassLoader(), new Class[]{TestClass.class}, cachingHandler);
        // First cache some results.
        proxy.method1();
        proxy.method1();
        Assert.assertEquals(1, base.numberTimesMethod1Called);
        // Now invalidate and call again a few times, there should only be one more call to the base.
        cachingInterceptor.invalidate();
        proxy.method1();
        proxy.method1();
        Assert.assertEquals(2, base.numberTimesMethod1Called);
        // Change the uid and cache some results
        uidSupplier.setUid("b");
        proxy.method1();
        proxy.method1();
        Assert.assertEquals(3, base.numberTimesMethod1Called);
        // Invalidate and call again
        cachingInterceptor.invalidate();
        proxy.method1();
        proxy.method1();
        Assert.assertEquals(4, base.numberTimesMethod1Called);
        // Change the uid and call again, the cached result should still be present from before.
        uidSupplier.setUid("a");
        proxy.method1();
        proxy.method1();
        Assert.assertEquals(4, base.numberTimesMethod1Called);
    }

    private final InvocationHandler forBaseWithInterceptor(final Object base, final MethodInterceptor interceptor) {
        return new InvocationHandler() {
            public Object invoke(final Object proxy, final Method method, Object[] args) throws Throwable {
                final Object[] arguments = args == null ? new Object[0] : args;
                return interceptor.invoke(new MethodInvocation() {
                    public Method getMethod() {
                        return method;
                    }

                    public Object[] getArguments() {
                        return arguments;
                    }

                    public Object proceed() throws Throwable {
                        return method.invoke(base, arguments);
                    }

                    public Object getThis() {
                        return base;
                    }

                    public AccessibleObject getStaticPart() {
                        return method;
                    }
                });
            }
        };
    }

    public interface TestClass {
        int method1();

        int method2(Integer by);
    }

    public static class TestClassImpl implements TestClass {
        int numberTimesMethod1Called = 0;
        int numberTimesMethod2Called = 0;

        public int method1() {
            ++numberTimesMethod1Called;
            return 0;
        }

        public int method2(Integer by) {
            ++numberTimesMethod2Called;
            return 1;
        }
    }

    public static class UidSupplierImpl implements UidSupplier {

        private String uid;

        public UidSupplierImpl setUid(final String uid) {
            this.uid = uid;
            return this;
        }

        @Override
        public String get() {
            return uid;
        }
    }


}
