package com.klinik.dev.events;

/**
 * Created by khairulimam on 31/01/17.
 */
public class EventBus {
  private static com.google.common.eventbus.EventBus bus;

  public static com.google.common.eventbus.EventBus getInstance() {
    if (bus == null)
      return bus = new com.google.common.eventbus.EventBus();
    return bus;
  }
}
