package com.bpce.lab.kafka;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class EventProducer {

    @Inject
    @Channel("events")
    Emitter<String> emitter;

    public void publish(String message) {
        emitter.send(message);
    }
}
