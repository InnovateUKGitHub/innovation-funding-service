package org.innovateuk.ifs;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.innovateuk.ifs.async.generation.AsyncFuturesGenerator;
import org.innovateuk.ifs.async.util.*;
import org.innovateuk.ifs.commons.service.ExceptionThrowingFunction;
import org.innovateuk.ifs.util.*;
import org.mockito.Matchers;
import org.mockito.stubbing.Answer;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * TODO DW - document this class
 */
public class AsyncTestExpectationHelper {

    /**
     * Setup expectations around the various {@link AsyncFuturesGenerator} methods that generate CompletableFutures
     * and helper classes
     */
    public static void setupAsyncExpectations(AsyncFuturesGenerator futuresGeneratorMock) {
        setAsyncMethodExpectations(futuresGeneratorMock);
        setupAwaitAllMethodExpectations(futuresGeneratorMock);

    }

    /**
     * Setup expectations around the various {@link AsyncFuturesGenerator#async} methods such that they will invoke
     * their callbacks immediately and return a completed future with the callback's result in it  (or simply invoke
     * the callback if it has no return value)
     * @param futuresGeneratorMock
     */
    private static void setAsyncMethodExpectations(AsyncFuturesGenerator futuresGeneratorMock) {

        Answer futureSupplierAnswer = invocation -> {
            ExceptionThrowingSupplier supplier = (ExceptionThrowingSupplier) invocation.getArguments()[0];
            return CompletableFuture.completedFuture(supplier.get());
        };

        Answer<Object> futureConsumerAnswer = invocation -> {
            ExceptionThrowingRunnable supplier = (ExceptionThrowingRunnable) invocation.getArguments()[0];
            supplier.run();
            return CompletableFuture.completedFuture(null);
        };

        when(futuresGeneratorMock.async(isA(ExceptionThrowingSupplier.class))).thenAnswer(futureSupplierAnswer);
        when(futuresGeneratorMock.async(isA(String.class), isA(ExceptionThrowingSupplier.class))).thenAnswer(futureSupplierAnswer);
        when(futuresGeneratorMock.async(isA(ExceptionThrowingRunnable.class))).thenAnswer(futureConsumerAnswer);
        when(futuresGeneratorMock.async(isA(String.class), isA(ExceptionThrowingRunnable.class))).thenAnswer(futureConsumerAnswer);
    }

    /**
     * Setup expectations around the various {@link AsyncFuturesGenerator#awaitAll} methods such that they will invoke
     * their callbacks immediately with the arguments they require and return a completed future with the callback's
     * result in it (or simply invoke the callback if it has no return value)
     * @param futuresGeneratorMock
     */
    private static void setupAwaitAllMethodExpectations(AsyncFuturesGenerator futuresGeneratorMock) {
        setupAwaitAllWithOneFutureExpectations(futuresGeneratorMock);
        setupAwaitAllWithTwoFuturesExpectations(futuresGeneratorMock);
        setupAwaitAllWithThreeFuturesExpectations(futuresGeneratorMock);
        setupAwaitAllWithFourFuturesExpectations(futuresGeneratorMock);
        setupAwaitAllWithNFuturesExpectations(futuresGeneratorMock);
    }

    private static void setupAwaitAllWithOneFutureExpectations(AsyncFuturesGenerator futuresGeneratorMock) {
        // expectations for when awaitAll() is called with a single future
        Answer<Object> tuple1HandlerAnswer = invocation -> {

            CompletableFuture<?> future1 = (CompletableFuture<?>) invocation.getArguments()[0];

            CompletableFutureTuple1Handler tupleFutureHandler = mock(CompletableFutureTuple1Handler.class);

            // expectations for when thenApply() is called
            when(tupleFutureHandler.thenApply(isA(Function.class))).thenAnswer(thenApplyInvocation -> {

                Function function = (Function) thenApplyInvocation.getArguments()[0];
                return CompletableFuture.completedFuture(function.apply(future1.get()));
            });

            // expectations for when thenAccept() is called
            when(tupleFutureHandler.thenAccept(isA(ExceptionThrowingConsumer.class))).thenAnswer(thenAnswerInvocation -> {

                Consumer consumer = (Consumer) thenAnswerInvocation.getArguments()[0];
                consumer.accept(future1.get());
                return CompletableFuture.completedFuture(null);
            });

            // expectations for when thenReturn() is called
            when(tupleFutureHandler.thenReturn()).thenReturn(future1.get());

            return tupleFutureHandler;
        };

        when(futuresGeneratorMock.awaitAll(isA(CompletableFuture.class))).thenAnswer(tuple1HandlerAnswer);
        when(futuresGeneratorMock.awaitAll(isA(String.class), isA(CompletableFuture.class))).thenAnswer(tuple1HandlerAnswer);
    }

    private static void setupAwaitAllWithTwoFuturesExpectations(AsyncFuturesGenerator futuresGeneratorMock) {
        // expectations for when awaitAll() is called with 2 futures
        Answer<Object> tuple2HanderAnswer = invocation -> {

            CompletableFuture<?> future1 = (CompletableFuture<?>) invocation.getArguments()[0];
            CompletableFuture<?> future2 = (CompletableFuture<?>) invocation.getArguments()[1];

            CompletableFutureTuple2Handler tupleFutureHandler = mock(CompletableFutureTuple2Handler.class);

            // expectations for when thenApply() is called
            when(tupleFutureHandler.thenApply(isA(BiFunction.class))).thenAnswer(thenApplyInvocation -> {

                BiFunction function = (BiFunction) thenApplyInvocation.getArguments()[0];
                return CompletableFuture.completedFuture(function.apply(future1.get(), future2.get()));
            });

            // expectations for when thenAccept() is called
            when(tupleFutureHandler.thenAccept(isA(BiConsumer.class))).thenAnswer(thenAnswerInvocation -> {

                BiConsumer consumer = (BiConsumer) thenAnswerInvocation.getArguments()[0];
                consumer.accept(future1.get(), future2.get());
                return CompletableFuture.completedFuture(null);
            });

            // expectations for when thenReturn() is called
            when(tupleFutureHandler.thenReturn()).thenReturn(Pair.of(future1.get(), future2.get()));

            return tupleFutureHandler;
        };

        when(futuresGeneratorMock.awaitAll(isA(CompletableFuture.class), isA(CompletableFuture.class))).thenAnswer(tuple2HanderAnswer);
        when(futuresGeneratorMock.awaitAll(isA(String.class), isA(CompletableFuture.class), isA(CompletableFuture.class))).thenAnswer(tuple2HanderAnswer);
    }

    private static void setupAwaitAllWithThreeFuturesExpectations(AsyncFuturesGenerator futuresGeneratorMock) {
        // expectations for when awaitAll() is called with 3 futures
        Answer<Object> tuple3HandlerAnswer = invocation -> {

            CompletableFuture<?> future1 = (CompletableFuture<?>) invocation.getArguments()[0];
            CompletableFuture<?> future2 = (CompletableFuture<?>) invocation.getArguments()[1];
            CompletableFuture<?> future3 = (CompletableFuture<?>) invocation.getArguments()[2];

            CompletableFutureTuple3Handler tupleFutureHandler = mock(CompletableFutureTuple3Handler.class);

            // expectations for when thenApply() is called
            when(tupleFutureHandler.thenApply(isA(TriFunction.class))).thenAnswer(thenApplyInvocation -> {

                TriFunction function = (TriFunction) thenApplyInvocation.getArguments()[0];
                return CompletableFuture.completedFuture(function.apply(future1.get(), future2.get(), future3.get()));
            });

            // expectations for when thenAccept() is called
            when(tupleFutureHandler.thenAccept(isA(TriConsumer.class))).thenAnswer(thenAnswerInvocation -> {

                TriConsumer consumer = (TriConsumer) thenAnswerInvocation.getArguments()[0];
                consumer.accept(future1.get(), future2.get(), future3.get());
                return CompletableFuture.completedFuture(null);
            });

            // expectations for when thenReturn() is called
            when(tupleFutureHandler.thenReturn()).thenReturn(Triple.of(future1.get(), future2.get(), future2.get()));

            return tupleFutureHandler;
        };

        when(futuresGeneratorMock.awaitAll(isA(CompletableFuture.class), isA(CompletableFuture.class), isA(CompletableFuture.class))).thenAnswer(tuple3HandlerAnswer);
        when(futuresGeneratorMock.awaitAll(isA(String.class), isA(CompletableFuture.class), isA(CompletableFuture.class), isA(CompletableFuture.class))).thenAnswer(tuple3HandlerAnswer);
    }

    private static void setupAwaitAllWithFourFuturesExpectations(AsyncFuturesGenerator futuresGeneratorMock) {
        // expectations for when awaitAll() is called with 3 futures
        Answer<Object> tuple4HandlerAnswer = invocation -> {

            CompletableFuture<?> future1 = (CompletableFuture<?>) invocation.getArguments()[0];
            CompletableFuture<?> future2 = (CompletableFuture<?>) invocation.getArguments()[1];
            CompletableFuture<?> future3 = (CompletableFuture<?>) invocation.getArguments()[2];
            CompletableFuture<?> future4 = (CompletableFuture<?>) invocation.getArguments()[3];

            CompletableFutureTuple4Handler tupleFutureHandler = mock(CompletableFutureTuple4Handler.class);

            // expectations for when thenApply() is called
            when(tupleFutureHandler.thenApply(isA(QuadFunction.class))).thenAnswer(thenApplyInvocation -> {

                TriFunction function = (TriFunction) thenApplyInvocation.getArguments()[0];
                return CompletableFuture.completedFuture(function.apply(future1.get(), future2.get(), future3.get()));
            });

            // expectations for when thenAccept() is called
            when(tupleFutureHandler.thenAccept(isA(QuadConsumer.class))).thenAnswer(thenAnswerInvocation -> {

                TriConsumer consumer = (TriConsumer) thenAnswerInvocation.getArguments()[0];
                consumer.accept(future1.get(), future2.get(), future3.get());
                return CompletableFuture.completedFuture(null);
            });

            // expectations for when thenReturn() is called
            when(tupleFutureHandler.thenReturn()).thenReturn(Quad.of(future1.get(), future2.get(), future2.get(), future3.get()));

            return tupleFutureHandler;
        };

        when(futuresGeneratorMock.awaitAll(isA(CompletableFuture.class), isA(CompletableFuture.class), isA(CompletableFuture.class), isA(CompletableFuture.class))).thenAnswer(tuple4HandlerAnswer);
        when(futuresGeneratorMock.awaitAll(isA(String.class), isA(CompletableFuture.class), isA(CompletableFuture.class), isA(CompletableFuture.class), isA(CompletableFuture.class))).thenAnswer(tuple4HandlerAnswer);
    }

    private static void setupAwaitAllWithNFuturesExpectations(AsyncFuturesGenerator futuresGeneratorMock) {

        // expectations for when awaitAll() is called with n futures with varargs
        Answer<Object> tupleNFromVarargsAnswer = invocation -> {

            List<CompletableFuture<?>> futures = stream(invocation.getArguments()).map(a -> (CompletableFuture<?>) a).collect(toList());
            return createTupleNHandlerMockFromFutureList(futures);
        };

        when(futuresGeneratorMock.awaitAll(isA(CompletableFuture.class), isA(CompletableFuture.class), isA(CompletableFuture.class), isA(CompletableFuture.class), Matchers.<CompletableFuture> anyVararg())).thenAnswer(tupleNFromVarargsAnswer);
        when(futuresGeneratorMock.awaitAll(isA(String.class), isA(CompletableFuture.class), isA(CompletableFuture.class), isA(CompletableFuture.class), isA(CompletableFuture.class), Matchers.<CompletableFuture> anyVararg())).thenAnswer(tupleNFromVarargsAnswer);

        // expectations for when awaitAll() is called with n futures with a List
        Answer<Object> tupleNFromListAnswer = invocation -> {

            List<CompletableFuture<?>> futures = (List<CompletableFuture<?>>) invocation.getArguments()[0];
            return createTupleNHandlerMockFromFutureList(futures);
        };

        when(futuresGeneratorMock.awaitAll(isA(List.class))).thenAnswer(tupleNFromListAnswer);
        when(futuresGeneratorMock.awaitAll(isA(String.class), isA(List.class))).thenAnswer(tupleNFromListAnswer);
    }

    private static Object createTupleNHandlerMockFromFutureList(List<CompletableFuture<?>> futures) {

        List<?> futureResults = simpleMap(futures, f -> {
            try {
                return f.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        CompletableFutureTupleNHandler tupleFutureHandler = mock(CompletableFutureTupleNHandler.class);

        // expectations for when thenApply() is called
        when(tupleFutureHandler.thenApply(isA(ExceptionThrowingFunction.class))).thenAnswer(thenApplyInvocation -> {

            ExceptionThrowingFunction function = (ExceptionThrowingFunction) thenApplyInvocation.getArguments()[0];
            return CompletableFuture.completedFuture(function.apply(futureResults));
        });

        // expectations for when thenAccept() is called
        when(tupleFutureHandler.thenAccept(isA(ExceptionThrowingConsumer.class))).thenAnswer(thenAnswerInvocation -> {

            ExceptionThrowingConsumer consumer = (ExceptionThrowingConsumer) thenAnswerInvocation.getArguments()[0];
            consumer.accept(futureResults);
            return CompletableFuture.completedFuture(null);
        });

        return tupleFutureHandler;
    }
}
