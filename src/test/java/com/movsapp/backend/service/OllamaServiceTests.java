package com.movsapp.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.http.HttpMethod.POST;

class OllamaServiceTests {
    private static final String ENDPOINT = "http://localhost:11434/api/generate";

    private MockRestServiceServer server;
    private OllamaService service;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        service = new OllamaService(builder, ENDPOINT, "qwen2.5:0.5b");
    }

    @Test
    void enviaElModeloConfiguradoSinStreaming() {
        server.expect(once(), requestTo(ENDPOINT))
            .andExpect(method(POST))
            .andExpect(content().json("""
                {"model":"qwen2.5:0.5b","prompt":"¿Cómo ha mejorado Java?","stream":false,"options":{"temperature":0.1,"top_p":0.6}}
                """))
            .andRespond(withSuccess("""
                {"model":"qwen2.5:0.5b","response":"Java ha evolucionado.","done":true}
                """, MediaType.APPLICATION_JSON));

        var response = service.generar("  ¿Cómo ha mejorado Java?  ");

        assertEquals("Java ha evolucionado.", response.response());
        server.verify();
    }
}
