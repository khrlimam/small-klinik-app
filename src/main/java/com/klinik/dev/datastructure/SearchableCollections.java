package com.klinik.dev.datastructure;

import com.klinik.dev.contract.Searchable;

import java.util.Comparator;
import java.util.List;

/**
 * Created by khairulimam on 01/02/17.
 */
public class SearchableCollections {
    public static void sort(List toBeSort, Searchable searchable) {
        java.util.Collections.sort(toBeSort, SearchableComparator.getInstance(searchable));
    }

    public static int binarySearch(List searchFrom, Searchable toBeSearch) {
        return java.util.Collections.binarySearch(searchFrom, toBeSearch, SearchableComparator.getInstance(toBeSearch));
    }

}
