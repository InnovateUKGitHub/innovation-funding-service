package org.innovateuk.ifs.async;

import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.locks.ReadWriteLock;

/**
 * Helper class for tests to inspect the internal state of ReadWriteLocks.
 */
public class ReadWriteLockTestHelper {

    public static boolean isReadLocked(ReadWriteLock lock) {
        Object readLockSync = ReflectionTestUtils.getField(lock.readLock(), "sync");
        return isReadLockEnabled(readLockSync);
    }

    public static boolean isWriteLocked(ReadWriteLock lock) {
        Object writeLockSync = ReflectionTestUtils.getField(lock.writeLock(), "sync");
        return isWriteLockEnabled(writeLockSync);
    }

    private static boolean isWriteLockEnabled(Object sync) {
        return (Boolean) ReflectionTestUtils.invokeMethod(sync, "isWriteLocked");
    }

    private static boolean isReadLockEnabled(Object sync) {
        return ((Integer) ReflectionTestUtils.invokeGetterMethod(sync, "readLockCount")) > 0;
    }
}
