package com.worth.ifs.application.service;


import org.springframework.ui.Model;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureAdapter;
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

    public static <S> Stream<S> call(Stream<ListenableFuture<S>> streamWithFutures){
        return streamWithFutures.map(call());
    }

    public static <S, T> Map<S, T> call(Map<S, Future<T>> mapWithFutures) {
        return mapWithFutures.entrySet().stream().
                collect(Collectors.toMap(
                        Entry::getKey,
                        entry -> ListenableFutures.<T>call().apply(entry.getValue())
                ));
    }

    public static <S> List<S> call(Collection<Future<S>> collectionWithFutures) {
        return collectionWithFutures.stream().map(call()).collect(toList());
    }

    public static <S> S call(Future<S> future) {
        return ListenableFutures.<S>call().apply(future);
    }

    public static <S> Function<Future<S>, S> call() {
        return f -> {
            try {
                return f.get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                // TODO is this the correct behaviour?
                throw new RuntimeException(e);
            }
        };
    }

    public static <T, S> ListenableFuture<T> adapt(ListenableFuture<S> base, Function<S, T> adaptor) {
        return new ListenableFutureAdapter<T, S>(base) {
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
