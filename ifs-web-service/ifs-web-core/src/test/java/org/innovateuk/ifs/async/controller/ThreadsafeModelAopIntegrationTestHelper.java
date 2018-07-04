package org.innovateuk.ifs.async.controller;

import org.innovateuk.ifs.async.annotations.AsyncMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.function.Consumer;

/**
 * Helper class to help {@link ThreadsafeModelAopIntegrationTest} to prove the AOP mechanisms that it is testing.
 */
@Controller
@RequestMapping("/ThreadsafeModelAopIntegrationTestHelper")
public class ThreadsafeModelAopIntegrationTestHelper {

    private Consumer<Model> modelConsumer;

    @AsyncMethod
    @GetMapping
    public void get(Model model) {
        modelConsumer.accept(model);
    }

    @AsyncMethod
    @GetMapping("/get2")
    @SuppressWarnings("unused")
    public void getWithOtherParameters(int i, int i1, Model model, int i2) {
        modelConsumer.accept(model);
    }

    @AsyncMethod
    @GetMapping("/get3")
    @SuppressWarnings("unused")
    public void getWithExtendedModelMap(int i, int i1, ExtendedModelMap model, int i2) {
        modelConsumer.accept(model);
    }

    @AsyncMethod
    @GetMapping("/get4")
    @SuppressWarnings("unused")
    public void getWithRedirectAttributes(int i, int i1, RedirectAttributes model, int i2) {
        modelConsumer.accept(model);
    }

    @GetMapping("/get5")
    public void post(Model model) {
        modelConsumer.accept(model);
    }

    public void nonRequestHandling(Model model) {
        modelConsumer.accept(model);
    }

    void setModelConsumer(Consumer<Model> modelConsumer) {
        this.modelConsumer = modelConsumer;
    }
}
