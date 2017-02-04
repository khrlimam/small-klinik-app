package com.klinik.dev.datastructure;

import com.klinik.dev.contract.Comparable;

import java.util.Collections;
import java.util.List;

/**
 * Created by khairulimam on 01/02/17.
 */
public class ComparableCollections {
    public static void sort(List toBeSort, Comparable comparable) {
        Collections.sort(toBeSort,  ComparableComparator.getInstance(comparable));
    }

    public static int binarySearch(List searchFrom, Comparable toBeSearch) {
        return Collections.binarySearch(searchFrom, toBeSearch, ComparableComparator.getInstance(toBeSearch));
    }

}
