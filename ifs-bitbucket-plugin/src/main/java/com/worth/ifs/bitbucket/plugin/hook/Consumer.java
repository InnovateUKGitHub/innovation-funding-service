package com.worth.ifs.bitbucket.plugin.hook;

/**
 * A consumer.
 * @param <T>
 */
public interface Consumer<T> {

    void accept(T t);

}
