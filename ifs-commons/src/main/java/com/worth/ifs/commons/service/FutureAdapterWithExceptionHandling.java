package com.worth.ifs.commons.service;


import com.worth.ifs.util.Either;
import org.springframework.util.concurrent.FutureAdapter;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

public class FutureAdapterWithExceptionHandling<T, S> extends FutureAdapter<T, S> {

    private final Object mutex = new Object();

    private final Function<Throwable, Either<?, T>> handler;
    private final Function<S, T> mapper;

    public FutureAdapterWithExceptionHandling(final Future<S> adaptee, final Function<S, T> mapper, final Function<Throwable, Either<?, T>> handler) {
        super(adaptee);
        this.handler = handler;
        this.mapper = mapper;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        synchronized (this.mutex) {
            try {
                return super.get();
            } catch (ExecutionException e) {
                final Either<?, T> handled = handler.apply(e.getCause());
                if (handled.isRight()) {
                    return handled.getRight();
                } else {
                    throw e;
                }
            }
        }
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        synchronized (this.mutex) {
            try {
                return super.get(timeout, unit);
            } catch (ExecutionException e) {
                final Either<?, T> handled = handler.apply(e.getCause());
                if (handled.isRight()) {
                    throw e;
                } else {
                    return handled.getRight();
                }
            }
        }
    }

    @Override
    protected T adapt(S adapteeResult) throws ExecutionException {
        return mapper.apply(adapteeResult);
    }

}
