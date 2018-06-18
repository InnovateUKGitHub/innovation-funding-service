package org.innovateuk.ifs.async.controller;

import org.innovateuk.ifs.async.annotations.AsyncMethod;
import org.innovateuk.ifs.async.generation.AsyncAdaptor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

/**
 * Test Controller to aid in testing with {@link AwaitAsyncFuturesCompletionIntegrationTest}.
 */
@Controller
@RequestMapping("/AwaitAllFuturesCompletionIntegrationTestHelper")
public class AwaitAsyncFuturesCompletionIntegrationTestHelper extends AsyncAdaptor {

    @AsyncMethod
    @GetMapping
    public void asyncAnnotatedMethod(List<String> futuresCompleted) {
        createFutures(futuresCompleted);
    }

    @AsyncMethod
    @GetMapping("/2")
    public void getMethodWithFutureAddedToModel(Model model) {

        model.addAttribute("futureResult", CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(20);
                return "theResult";
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    @GetMapping("/3")
    public void nonAsyncAnnotatedethod(List<String> futuresCompleted) {
        createFutures(futuresCompleted);
    }

    private void createFutures(List<String> futuresCompleted) {
        CompletableFuture<Void> future1 = async(() -> {
            Thread.sleep(20);
            futuresCompleted.add("future1");
        });

        CompletableFuture<Void> future2 = async(() -> {
            futuresCompleted.add("future2");
            Thread.sleep(30);
        });

        awaitAll(future1).thenAccept(f1 -> {
            futuresCompleted.add("awaitingFuture");

            CompletableFuture<Void> awaitingFutureChildFuture = async(() -> {
                futuresCompleted.add("awaitingFutureChildFuture");

                CompletableFuture<Void> awaitingFutureChildFutureChild = async(() -> {
                    Thread.sleep(10);
                    futuresCompleted.add("awaitingFutureChildFutureChild");
                });
            });

        });
    }

    @DeleteMapping
    public void delete(CountDownLatch latch) {
        async(() -> latch.await());
    }
}
