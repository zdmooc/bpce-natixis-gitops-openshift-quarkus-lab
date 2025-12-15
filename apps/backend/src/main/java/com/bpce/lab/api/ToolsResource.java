package com.bpce.lab.api;

import java.time.Instant;
import java.util.Map;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/tools")
public class ToolsResource {

    @GET
    @Path("/info")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> info() {
        return Map.of(
                "service", "container-tools-api",
                "timestamp", Instant.now().toString(),
                "env", System.getenv().getOrDefault("ENV", "local"),
                "gitSha", System.getenv().getOrDefault("GIT_SHA", "dev"),
                "kafkaBootstrap", System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092")
        );
    }
}
