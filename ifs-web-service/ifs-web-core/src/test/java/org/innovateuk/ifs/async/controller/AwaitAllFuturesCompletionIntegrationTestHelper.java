package org.innovateuk.ifs.async.controller;

import org.innovateuk.ifs.async.util.AsyncAdaptor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Test Controller to aid in testing with {@link AwaitAllFuturesCompletionIntegrationTest}.
 */
@Controller
@RequestMapping("/AwaitAllFuturesCompletionIntegrationTestHelper")
public class AwaitAllFuturesCompletionIntegrationTestHelper extends AsyncAdaptor {

    @GetMapping
    public String getMethod(List<String> futuresCompleted) {

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

        return null;
    }
}
