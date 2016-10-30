package com.worth.ifs.abstractions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by worth on 30/10/2016.
 */
public abstract class CList extends ArrayList {

    public static List<Integer> list(int from, int to) {
        List<Integer> list = new ArrayList<>();
        for(int i=from; i<= to; i++) {
            list.add(i);
        }
        return list;
    }

    public static <T> List<T> listOf(int n, Supplier<T> itemSupplier) {
        List<T> list = new ArrayList<>();
        for (int i=0; i < n; i++) {
            list.add(itemSupplier.get());
        }
        return list;
    }

}
