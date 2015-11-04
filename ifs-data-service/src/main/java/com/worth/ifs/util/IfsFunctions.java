package com.worth.ifs.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class to provide useful reusable Functions throughout the codebase
 */
public class IfsFunctions {

    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog(IfsFunctions.class);

    public static <T> List<T> flattenLists(List<List<T>> lists) {
        return lists.stream()
                .flatMap(l -> l.stream())
                .collect(Collectors.toList());
    }

    public static <T> List<T> combineLists(List<T>... lists) {

        List<T> combinedList = new ArrayList<>();

        for (List<T> list : lists) {
            combinedList.addAll(list);
        }

        return combinedList;
    }
}
