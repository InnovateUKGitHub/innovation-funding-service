package org.innovateuk.ifs.testdata.builders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private EntityManager em;

    @Transactional
    @Override
    public void doWithinTransaction(Runnable runnable) {
        runnable.run();
    }

    @Transactional
    @Override
    public <T> T doWithinTransaction(Supplier<T> supplier) {
        return supplier.get();
    }

    @Async
    @Override
    public <T> CompletableFuture<T> async(Supplier<T> supplier) {
        return CompletableFuture.completedFuture(supplier.get());
    }

    @Async
    @Override
    public CompletableFuture<Void> async(Runnable runnable) {
        runnable.run();
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> nonAsync(Runnable runnable) {
        runnable.run();
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void flushAndClearSession() {
        em.flush();
        em.clear();
    }
}
