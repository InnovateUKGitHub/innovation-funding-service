package com.worth.ifs.testdata.builders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.function.Supplier;

@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private EntityManager em;

    @Override
    public void doWithinTransaction(Runnable runnable) {
        runnable.run();
    }

    @Override
    public <T> T doWithinTransaction(Supplier<T> supplier) {
        return supplier.get();
    }

    @Override
    public void flushAndClearSession() {
        em.flush();
        em.clear();
    }
}
