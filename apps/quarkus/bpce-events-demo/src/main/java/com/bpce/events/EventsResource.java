package com.bpce.events;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@Path("/events")
@ApplicationScoped
public class EventsResource {

  @Channel("events-out")
  Emitter<String> emitter;

  @POST
  @Consumes(MediaType.TEXT_PLAIN)
  public Response send(String body) {
    emitter.send(body == null ? "" : body);
    return Response.accepted().build();
  }
}
