package org.innovateuk.ifs.async.controller;

/**
 * A ThreadLocal that allows us to test whether or not async code is allowed to be executed within the current callstack
 */
public final class AsyncAllowedThreadLocal {

    private static ThreadLocal<Boolean> ASYNC_ALLOWED = new ThreadLocal<>();

    private AsyncAllowedThreadLocal() {}

    public static boolean isAsyncAllowed() {
        Boolean allowed = ASYNC_ALLOWED.get();
        return allowed != null ? allowed : false;
    }

    public static void setAsyncAllowed(boolean allowed) {
        ASYNC_ALLOWED.set(allowed);
    }

    public static void clearAsyncAllowed() {
        ASYNC_ALLOWED.remove();
    }
}
