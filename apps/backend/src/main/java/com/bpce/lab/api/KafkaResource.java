package com.bpce.lab.api;

import com.bpce.lab.kafka.EventProducer;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/kafka")
public class KafkaResource {

    @Inject
    EventProducer producer;

    @POST
    @Path("/publish")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response publish(@QueryParam("key") String key, String body) {
        // key is here for future evolution; keeping message simple
        producer.publish((key == null ? "no-key" : key) + "|" + body);
        return Response.accepted().build();
    }
}
