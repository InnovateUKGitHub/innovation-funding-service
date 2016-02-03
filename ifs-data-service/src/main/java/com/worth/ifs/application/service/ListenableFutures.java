package com.worth.ifs.application.service;


import org.springframework.ui.Model;

import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
            else {
                modelWithFutures.addAttribute(key, value); // TODO remove this.
            }
        }
    }

}
