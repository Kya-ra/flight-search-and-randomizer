package ie.tcd.scss.flight_scout.controller;

import ie.tcd.scss.flight_scout.model.Flight;
import ie.tcd.scss.flight_scout.model.FlightSearchResponse;
import ie.tcd.scss.flight_scout.service.FlightSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for FlightController searchFlights endpoint.
 */
@WebMvcTest(FlightController.class)
@AutoConfigureMockMvc(addFilters = false)
class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlightSearchService flightSearchService;

    @Test
    void searchFlights_WithValidParams_ReturnsFlights() throws Exception {
       
        Flight mockFlight = new Flight();
        mockFlight.setOrigin("DUB");
        mockFlight.setDestination("BCN");
        mockFlight.setPrice(150.0);
        mockFlight.setAirline("Ryanair");

        FlightSearchResponse mockResponse = new FlightSearchResponse();
        mockResponse.setOrigin("DUB");
        mockResponse.setDestination("BCN");
        mockResponse.setOutboundDate("2025-06-01");
        mockResponse.setTotalResults(1);
        mockResponse.setCheapestPrice(150.0);
        mockResponse.setFlights(Collections.singletonList(mockFlight));

        when(flightSearchService.searchFlights(
                eq("DUB"), eq("BCN"), eq("2025-06-01"), eq(2),
                any(), any(), any(), any(), any(), any(), any()
        )).thenReturn(mockResponse);

        
        mockMvc.perform(get("/api/flights/search")
                        .param("origin", "DUB")
                        .param("destination", "BCN")
                        .param("flight_type", "2")
                        .param("outboundDate", "2025-06-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.origin").value("DUB"))
                .andExpect(jsonPath("$.destination").value("BCN"))
                .andExpect(jsonPath("$.totalResults").value(1))
                .andExpect(jsonPath("$.flights", hasSize(1)));

        verify(flightSearchService, times(1)).searchFlights(
                eq("DUB"), eq("BCN"), eq("2025-06-01"), eq(2),
                any(), any(), any(), any(), any(), any(), any()
        );
    }

    @Test
    void searchFlights_MissingRequiredParam_ReturnsBadRequest() throws Exception {
        // Missing origin parameter
        mockMvc.perform(get("/api/flights/search")
                        .param("destination", "BCN")
                        .param("flight_type", "2")
                        .param("outboundDate", "2025-06-01"))
                .andExpect(status().isBadRequest());

        verify(flightSearchService, never()).searchFlights(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()
        );
    }

    @Test
    void searchFlights_NoFlightsFound_ReturnsEmptyList() throws Exception {
        FlightSearchResponse emptyResponse = new FlightSearchResponse();
        emptyResponse.setOrigin("DUB");
        emptyResponse.setDestination("XYZ");
        emptyResponse.setTotalResults(0);
        emptyResponse.setFlights(Collections.emptyList());

        when(flightSearchService.searchFlights(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()
        )).thenReturn(emptyResponse);

        mockMvc.perform(get("/api/flights/search")
                        .param("origin", "DUB")
                        .param("destination", "XYZ")
                        .param("flight_type", "2")
                        .param("outboundDate", "2025-06-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalResults").value(0))
                .andExpect(jsonPath("$.flights", hasSize(0)));
    }
}
