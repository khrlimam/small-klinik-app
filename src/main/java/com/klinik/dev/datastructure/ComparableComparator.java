package com.klinik.dev.datastructure;

import com.klinik.dev.contract.Comparable;

import java.util.Comparator;

/**
 * Created by khairulimam on 01/02/17.
 */
public class ComparableComparator {
  public static java.util.Comparator getInstance(Comparable comparable) {
    return (Comparator<Comparable>) (o1, o2) -> o1.toBeCompared() - o2.toBeCompared();
  }
}