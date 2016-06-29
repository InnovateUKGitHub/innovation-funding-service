package com.worth.ifs.bitbucket.plugin.hook;

import java.util.Comparator;
import java.util.List;


public class FlywayVersionComparator implements Comparator<List<Integer>> {
    @Override
    public int compare(final List<Integer> o1,final List<Integer> o2) {
        if (o1.isEmpty() && o2.isEmpty()){
            return 0;
        } else if (o1.isEmpty()){
            return -1;
        } else if (o2.isEmpty()) {
            return 1;
        } else if (o1.get(0) == o2.get(0)){
            return compare(o1.subList(1, o1.size()), o2.subList(1, o2.size()));
        } else {
            return o1.get(0).compareTo(o2.get(0));
        }
    }
}
