package com.bpce.events;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class EventsConsumer {
  private static final Logger LOG = Logger.getLogger(EventsConsumer.class);

  @Incoming("events-in")
  public void consume(String msg) {
    LOG.infov("bpce-events-demo received: {0}", msg);
  }
}
