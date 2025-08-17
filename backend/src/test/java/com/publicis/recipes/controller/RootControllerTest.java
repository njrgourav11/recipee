package com.publicis.recipes.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RootControllerTest {

    @InjectMocks
    private RootController rootController;

    @Test
    void root_ReturnsApiInformation() {
        ResponseEntity<Map<String, Object>> response = rootController.root();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertEquals("Recipe Management System API", body.get("application"));
        assertEquals("1.0.0", body.get("version"));
        assertEquals("running", body.get("status"));
        assertEquals("/api", body.get("apiBase"));
        assertNotNull(body.get("endpoints"));
    }

    @Test
    void root_ContainsEndpointsMap() {
        ResponseEntity<Map<String, Object>> response = rootController.root();

        assertNotNull(response);
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        
        Object endpoints = body.get("endpoints");
        assertNotNull(endpoints);
        assertTrue(endpoints instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> endpointsMap = (Map<String, Object>) endpoints;
        assertTrue(endpointsMap.containsKey("recipes"));
        assertTrue(endpointsMap.containsKey("statistics"));
        assertTrue(endpointsMap.containsKey("load"));
        assertTrue(endpointsMap.containsKey("swagger"));
    }

    @Test
    void root_ReturnsCorrectContentType() {
        ResponseEntity<Map<String, Object>> response = rootController.root();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }
}