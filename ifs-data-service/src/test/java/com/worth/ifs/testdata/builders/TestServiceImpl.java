package com.worth.ifs.testdata.builders;

import org.springframework.stereotype.Service;

import java.util.function.Supplier;

/**
 * TODO DW - document this class
 */
@Service
public class TestServiceImpl implements TestService {

    @Override
    public void doWithinTransaction(Runnable runnable) {
        runnable.run();
    }

    @Override
    public <T> T doWithinTransaction(Supplier<T> supplier) {
        return supplier.get();
    }
}
