package com.klinik.dev.datastructure;

import com.klinik.dev.contract.Searchable;

import java.util.Comparator;

/**
 * Created by khairulimam on 01/02/17.
 */
public class SearchableComparator {
    public static java.util.Comparator getInstance(Searchable searchable) {
        return (Comparator<Searchable>) (o1, o2) -> o1.getInt() - o2.getInt();
    }
}