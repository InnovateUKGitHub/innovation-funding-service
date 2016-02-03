package com.worth.ifs.application.service;


import org.springframework.ui.Model;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureAdapter;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;

public class ListenableFutures {

    public static void callAllFutures(Model modelWithFutures) throws InterruptedException {
        for (Entry<String, Object> entry : modelWithFutures.asMap().entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Future) {
                try {
                    modelWithFutures.addAttribute(key, ((Future) value).get());
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static <S, T> Map<S, T> call(Map<S, Future<T>> mapWithFutures) {
        Map<S, T> called = new HashMap<>();
        for (Entry<S, Future<T>> entry : mapWithFutures.entrySet()) {
            S key = entry.getKey();
            Future<T> value = entry.getValue();
            try {
                called.put(key, value.get());
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e){
                // TODO is this the correct behaviour?
                throw new RuntimeException(e);
            }
        }
        return called;
    }

    public static <T, S> ListenableFuture<T> adapt(ListenableFuture<S> base, Function<S, T> adaptor) {
        return new ListenableFutureAdapter<T, S>(base) {
            @Override
            protected T adapt(S adapteeResult) throws ExecutionException {
                return adaptor.apply(adapteeResult);
            }
        };
    }

    public static <T> ListenableFuture<T> settable(T toSet){
        SettableListenableFuture<T> settable = new SettableListenableFuture<>();
        settable.set(toSet);
        return settable;
    }

}
