package org.innovateuk.ifs.async.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.function.Consumer;

/**
 * Helper class to help {@link ThreadsafeModelAopIntegrationTest} to prove the AOP mechanisms that it is testing.
 */
@Controller("/ThreadsafeModelAopIntegrationTestHelper")
public class ThreadsafeModelAopIntegrationTestHelper {

    private Consumer<Model> modelConsumer;

    @GetMapping
    public void get(Model model) {
        modelConsumer.accept(model);
    }

    @GetMapping("/get2")
    @SuppressWarnings("unused")
    public void getWithOtherParameters(int i, int i1, Model model, int i2) {
        modelConsumer.accept(model);
    }

    @PostMapping
    public void post(Model model) {
        modelConsumer.accept(model);
    }

    @PutMapping
    public void put(Model model) {
        modelConsumer.accept(model);
    }

    @DeleteMapping
    public void delete(Model model) {
        modelConsumer.accept(model);
    }

    public void nonRequestHandling(Model model) {
        modelConsumer.accept(model);
    }

    void setModelConsumer(Consumer<Model> modelConsumer) {
        this.modelConsumer = modelConsumer;
    }
}
