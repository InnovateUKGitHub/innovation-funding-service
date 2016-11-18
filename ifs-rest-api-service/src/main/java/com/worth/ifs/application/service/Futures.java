package com.worth.ifs.application.service;


import org.springframework.ui.Model;
import org.springframework.util.concurrent.FutureAdapter;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public final class Futures {

	private Futures() {}
	
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

    public static <S> Stream<S> call(Stream<Future<S>> streamWithFutures){
        return streamWithFutures.map(call());
    }

    public static <S, T> Map<S, T> call(Map<S, Future<T>> mapWithFutures) {
        return mapWithFutures.entrySet().stream().
                collect(Collectors.toMap(
                        Entry::getKey,
                        entry -> Futures.<T>call().apply(entry.getValue())
                ));
    }

    public static <S> List<S> call(Collection<Future<S>> collectionWithFutures) {
        return collectionWithFutures.stream().map(call()).collect(toList());
    }

    public static <S> S call(Future<S> future) {
        return Futures.<S>call().apply(future);
    }

    public static <S> Function<Future<S>, S> call() {
        return f -> {
            try {
                return f.get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * Adapt with a map Function. Note that there is no way of dealing with Exceptions in the adaptee, These will get
     * propagated as ExecutionExceptions.
     * @param base
     * @param adaptor
     * @param <T>
     * @param <S>
     * @return
     */
    public static <T, S> Future<T> adapt(Future<S> base, Function<S, T> adaptor) {
        return new FutureAdapter<T, S>(base) {
            @Override
            protected T adapt(S adapteeResult) throws ExecutionException {
                return adaptor.apply(adapteeResult);
            }
        };
    }

    public static <T> ListenableFuture<T> settable(T toSet) {
        SettableListenableFuture<T> settable = new SettableListenableFuture<>();
        settable.set(toSet);
        return settable;
    }

}
