package org.innovateuk.ifs.async.executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskDecorator;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.innovateuk.ifs.util.CollectionFunctions.zip;

/**
 * This class is a decorator for threads spawned for processing @Async blocks, and the main responsibility
 * of this class is to make available on the child threads important ThreadLocals from the parent thread
 * (e.g. Request Attributes, Spring Security Context).
 *
 * This class has all implementations of AsyncThreadLocalCopier autowired in, and each one is responsible
 * for copying over a specific ThreadLocal property onto a child thread, and then also for cleaning up afterwards.
 * This lifecycle is controlled by {@link AsyncTaskDecorator} as new @Async handling Threads are produced.
 */
@Component
public class AsyncTaskDecorator implements TaskDecorator {

    private static final Log LOG = LogFactory.getLog(AsyncTaskDecorator.class);

    @Autowired
    private List<AsyncThreadLocalCopier<?>> threadLocalCopiers;

    @Override
    public Runnable decorate(Runnable runnable) {

        List<?> originalThreadLocalValues = getParentThreadLocals();

        String parentThreadName = Thread.currentThread().getName();

        return () -> {
            try {

                if (LOG.isTraceEnabled()) {
                    String childThreadName = Thread.currentThread().getName();
                    LOG.trace("Thread " + parentThreadName + " spawning Thread " + childThreadName + "...");
                }

                copyParentThreadLocalsToChildThread(originalThreadLocalValues);
                runnable.run();

            } finally {
                clearThreadLocalsFromChildThread();
            }
        };
    }

    private List<?> getParentThreadLocals() {
        return simpleMap(threadLocalCopiers,
                AsyncThreadLocalCopier::getOriginalValueFromOriginalThread);
    }

    private void copyParentThreadLocalsToChildThread(List<?> originalThreadLocalValues) {
        zip(threadLocalCopiers, originalThreadLocalValues, AsyncTaskDecorator::setOriginalValue);
    }

    private void clearThreadLocalsFromChildThread() {
        threadLocalCopiers.forEach(copier -> {
            try {
                copier.clearCopiedValueFromAsyncThread();
            } catch (Exception e) {
                LOG.error("Error whilst clearing ThreadLocal from async Thread - continuing to next clearance", e);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private static <T> void setOriginalValue(AsyncThreadLocalCopier<T> copier, Object originalValue) {
        copier.setCopyOfOriginalValueOnAsyncThread((T) originalValue);
    }
}

