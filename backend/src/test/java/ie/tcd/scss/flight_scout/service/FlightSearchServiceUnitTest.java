package ie.tcd.scss.flight_scout.service;

import ie.tcd.scss.flight_scout.model.FlightSearchResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for FlightSearchService.
 * Uses mocked RestTemplate to avoid real API calls.
 */
@ExtendWith(MockitoExtension.class)
class FlightSearchServiceUnitTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private FlightSearchService flightSearchService;

    private static final String MOCK_API_RESPONSE = """
            {
                "best_flights": [
                    {
                        "price": 150,
                        "departure_token": "token123",
                        "booking_token": "book123",
                        "total_duration": 210,
                        "flights": [
                            {
                                "airline": "Ryanair",
                                "flight_number": "FR1234",
                                "departure_airport": { "date": "2025-06-01", "time": "08:00" },
                                "arrival_airport": { "date": "2025-06-01", "time": "11:30" }
                            }
                        ]
                    }
                ],
                "other_flights": []
            }
            """;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(flightSearchService, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(flightSearchService, "serpApiKey", "test-api-key");
    }

    @Test
    void searchFlights_ValidRequest_ReturnsFlights() {
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(MOCK_API_RESPONSE);

        FlightSearchResponse response = flightSearchService.searchFlights(
                "DUB", "BCN", "2025-06-01", 2,
                null, null, 1, 0, 1, null, "EUR"
        );

        assertNotNull(response);
        assertEquals("DUB", response.getOrigin());
        assertEquals("BCN", response.getDestination());
        assertEquals(1, response.getTotalResults());
        assertEquals("Ryanair", response.getFlights().get(0).getAirline());
        verify(restTemplate, times(1)).getForObject(anyString(), eq(String.class));
    }

    @Test
    void searchFlights_WithMaxBudget_FiltersExpensiveFlights() {
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(MOCK_API_RESPONSE);

        // Budget of 100 should filter out the 150 euro flight
        FlightSearchResponse response = flightSearchService.searchFlights(
                "DUB", "BCN", "2025-06-01", 2,
                null, null, 1, 0, 1, 100, "EUR"
        );

        assertNotNull(response);
        assertEquals(0, response.getTotalResults());
        assertTrue(response.getFlights().isEmpty());
    }

    @Test
    void searchFlights_EmptyApiResponse_ReturnsEmptyList() {
        String emptyResponse = """
                { "best_flights": [], "other_flights": [] }
                """;
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(emptyResponse);

        FlightSearchResponse response = flightSearchService.searchFlights(
                "DUB", "XYZ", "2025-06-01", 2,
                null, null, 1, 0, 1, null, "EUR"
        );

        assertNotNull(response);
        assertEquals(0, response.getTotalResults());
        assertTrue(response.getFlights().isEmpty());
    }
}
