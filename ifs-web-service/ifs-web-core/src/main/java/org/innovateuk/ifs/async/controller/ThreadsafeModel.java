package org.innovateuk.ifs.async.controller;

import org.springframework.ui.Model;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

/**
 * A threadsafe implementation of Spring's Model class, to prevent multiple threads from attempting to write to the
 * underlying HashMap simultaneously.  A ReadWriteLock is used to allow simultaneous reading from the Model so long as
 * nothing is attempting to write to the Model.  Writing to the underlying Model will temporarily block other threads
 * until write is complete.
 */
public class ThreadsafeModel implements Model {

    private Model model;
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    ThreadsafeModel(Model model) {
        this.model = model;
    }

    @Override
    public Model addAttribute(String attributeName, Object attributeValue) {
        return withWriteLock(() -> model.addAttribute(attributeName, attributeValue));
    }

    @Override
    public Model addAttribute(Object attributeValue) {
        return withWriteLock(() -> model.addAttribute(attributeValue));
    }

    @Override
    public Model addAllAttributes(Collection<?> attributeValues) {
        return withWriteLock(() -> model.addAllAttributes(attributeValues));
    }

    @Override
    public Model addAllAttributes(Map<String, ?> attributes) {
        return withWriteLock(() -> model.addAllAttributes(attributes));
    }

    @Override
    public Model mergeAttributes(Map<String, ?> attributes) {
        return withWriteLock(() -> model.mergeAttributes(attributes));
    }

    @Override
    public boolean containsAttribute(String attributeName) {
        return withReadLock(() -> model.containsAttribute(attributeName));
    }

    @Override
    public Map<String, Object> asMap() {
        return withReadLock(() -> model.asMap());
    }

    private <T> T withWriteLock(Supplier<T> supplier) {
        lock.writeLock().lock();
        try {
            return supplier.get();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private <T> T withReadLock(Supplier<T> supplier) {
        lock.readLock().lock();
        try {
            return supplier.get();
        } finally {
            lock.readLock().unlock();
        }
    }
}
