package com.bpce.lab.kafka;

import org.jboss.logging.Logger;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class EventConsumer {

    private static final Logger LOG = Logger.getLogger(EventConsumer.class);

    @Incoming("events-in")
    public void consume(String message) {
        LOG.infov("Kafka event received: {0}", message);
    }
}
