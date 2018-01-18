package org.innovateuk.ifs.async.executor;

/**
 * Implementations of this interface are responsible for copying ThreadLocal values from their current Thread onto a
 * child Thread, and clearing them away afterwards.  In this way, we can allow asynchronous blocks of code to
 * make REST calls to the data layer by virtue of having access to the remote user UID and Spring Security Context
 */
public interface AsyncThreadLocalCopier<T> {

    T getOriginalValueFromOriginalThread();

    void setCopyOfOriginalValueOnAsyncThread(T originalValue);

    void clearCopiedValueFromAsyncThread();
}
