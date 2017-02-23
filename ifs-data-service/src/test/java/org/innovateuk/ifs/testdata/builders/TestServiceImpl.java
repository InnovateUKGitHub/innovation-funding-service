package org.innovateuk.ifs.testdata.builders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
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

    @Override
    public void flushAndClearSession() {
        em.flush();
        em.clear();
    }
}
