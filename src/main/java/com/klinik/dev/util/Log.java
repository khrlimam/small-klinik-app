package com.klinik.dev.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by khairulimam on 27/01/17.
 */
public class Log {

  public static void i(Class c, String message) {
    Logger.getLogger(c.getSimpleName()).log(Level.INFO, message);
  }

  public static void w(Class c, String message) {
    Logger.getLogger(c.getSimpleName()).log(Level.WARNING, message);
  }
}
