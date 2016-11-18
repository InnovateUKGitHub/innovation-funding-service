package com.worth.ifs.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.addAll;

public final class StreamFunctions {
	
	private StreamFunctions() {}
	
    public static <T> Stream<T> toStream(T[] varargs){
        List<T> list = new ArrayList<>(varargs.length);
        addAll(list, varargs);
        return list.stream();
    }
}
