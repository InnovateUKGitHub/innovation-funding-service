package com.worth.ifs.bitbucket.plugin.hook;


public interface Consumer<T> {

    void accept(T t);

}
