package es.metrica.sept25.evolutivo.maps.geocode;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import es.metrica.sept25.evolutivo.entity.maps.reverseGeocode.ReverseGeocodeGroup;
import es.metrica.sept25.evolutivo.entity.maps.reverseGeocode.ReverseGeocodeResult;
import es.metrica.sept25.evolutivo.service.maps.geocode.ReverseGeocodeServiceImpl;

@ExtendWith(MockitoExtension.class)
class ReverseGeocodeServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ReverseGeocodeServiceImpl service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "API_KEY_GOOGLE", "fake-api-key");
    }


    @Test
    void getAddress_success_returnsAddress() {
        double lat = 40.4168;
        double lng = -3.7038;

        ReverseGeocodeResult result = new ReverseGeocodeResult();
        result.setFormatted_address("Calle Falsa 123, Madrid");

        ReverseGeocodeGroup response = new ReverseGeocodeGroup();
        response.setResults(new ReverseGeocodeResult[]{ result });

        when(restTemplate.getForObject(anyString(), eq(ReverseGeocodeGroup.class)))
                .thenReturn(response);

        Optional<String> addressOpt = service.getAddress(lat, lng);

        assertTrue(addressOpt.isPresent());
        assertEquals("Calle Falsa 123, Madrid", addressOpt.get());
    }

    @Test
    void getAddress_noResults_returnsEmpty() {
        ReverseGeocodeGroup response = new ReverseGeocodeGroup();
        response.setResults(new ReverseGeocodeResult[]{});

        when(restTemplate.getForObject(anyString(), eq(ReverseGeocodeGroup.class)))
                .thenReturn(response);

        Optional<String> addressOpt = service.getAddress(40.0, -3.0);

        assertTrue(addressOpt.isEmpty());
    }

    @Test
    void getAddress_nullResponse_returnsEmpty() {
        when(restTemplate.getForObject(anyString(), eq(ReverseGeocodeGroup.class)))
                .thenReturn(null);

        Optional<String> addressOpt = service.getAddress(40.0, -3.0);

        assertTrue(addressOpt.isEmpty());
    }
}
