package es.metrica.sept25.evolutivo.maps.geocode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import es.metrica.sept25.evolutivo.domain.dto.maps.geocode.AddressComponent;
import es.metrica.sept25.evolutivo.domain.dto.maps.geocode.Geocode;
import es.metrica.sept25.evolutivo.domain.dto.maps.geocode.GeocodeGroup;
import es.metrica.sept25.evolutivo.domain.dto.maps.geocode.GeocodeGroupAddress;
import es.metrica.sept25.evolutivo.domain.dto.maps.geocode.GeocodeResult;
import es.metrica.sept25.evolutivo.domain.dto.maps.geocode.GeocodeResultAddress;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.Coords;
import es.metrica.sept25.evolutivo.service.maps.geocode.GeocodeServiceImpl;

@ExtendWith(MockitoExtension.class)
class GeocodeServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GeocodeServiceImpl service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "API_KEY_GOOGLE", "fake-api-key");
    }

    /* =========================
     * getCoordinates
     * ========================= */

    @Test
    void getCoordinates_success_returnsCoords() {
        // GIVEN
        Coords coords = new Coords(40.4168, -3.7038);

        Geocode geocode = new Geocode();
        geocode.setLocation(coords);

        GeocodeResult geocodeResult = new GeocodeResult();
        geocodeResult.setGeometry(geocode);

        GeocodeGroup response = new GeocodeGroup();
        response.setResults(new GeocodeResult[]{ geocodeResult });

        when(restTemplate.getForObject(anyString(), eq(GeocodeGroup.class)))
                .thenReturn(response);

        // WHEN
        Optional<Coords> result = service.getCoordinates("Madrid");

        // THEN
        assertTrue(result.isPresent());
        assertEquals(40.4168, result.get().getLat());
        assertEquals(-3.7038, result.get().getLng());
    }

    @Test
    void getCoordinates_noResults_returnsEmpty() {
        GeocodeGroup response = new GeocodeGroup();
        response.setResults(new GeocodeResult[]{});

        when(restTemplate.getForObject(anyString(), eq(GeocodeGroup.class)))
                .thenReturn(response);

        Optional<Coords> result = service.getCoordinates("Unknown");

        assertTrue(result.isEmpty());
    }

    @Test
    void getCoordinates_nullResponse_returnsEmpty() {
        when(restTemplate.getForObject(anyString(), eq(GeocodeGroup.class)))
                .thenReturn(null);

        Optional<Coords> result = service.getCoordinates("Unknown");

        assertTrue(result.isEmpty());
    }

    /* =========================
     * getMunicipio
     * ========================= */

    @Test
    void getMunicipio_withLocality_returnsMunicipio() {
        AddressComponent component = new AddressComponent();
        component.setLong_name("Madrid");
        component.setTypes(List.of("locality"));

        GeocodeResultAddress resultAddress = new GeocodeResultAddress();
        resultAddress.setAddress_components(new AddressComponent[]{ component });

        GeocodeGroupAddress response = new GeocodeGroupAddress();
        response.setResults(new GeocodeResultAddress[]{ resultAddress });

        when(restTemplate.getForObject(anyString(), eq(GeocodeGroupAddress.class)))
                .thenReturn(response);

        Optional<String> municipio = service.getMunicipio(40.0, -3.0);

        assertTrue(municipio.isPresent());
        assertEquals("Madrid", municipio.get());
    }

    @Test
    void getMunicipio_withAdminLevel4_formatsMunicipio() {
        AddressComponent component = new AddressComponent();
        component.setLong_name("La Puebla");
        component.setTypes(List.of("administrative_area_level_4"));

        GeocodeResultAddress resultAddress = new GeocodeResultAddress();
        resultAddress.setAddress_components(new AddressComponent[]{ component });

        GeocodeGroupAddress response = new GeocodeGroupAddress();
        response.setResults(new GeocodeResultAddress[]{ resultAddress });

        when(restTemplate.getForObject(anyString(), eq(GeocodeGroupAddress.class)))
                .thenReturn(response);

        Optional<String> municipio = service.getMunicipio(40.0, -3.0);

        assertTrue(municipio.isPresent());
        assertEquals("Puebla, La", municipio.get());
    }

    @Test
    void getMunicipio_noValidTypes_returnsEmpty() {
        AddressComponent component = new AddressComponent();
        component.setLong_name("Spain");
        component.setTypes(List.of("country"));

        GeocodeResultAddress resultAddress = new GeocodeResultAddress();
        resultAddress.setAddress_components(new AddressComponent[]{ component });

        GeocodeGroupAddress response = new GeocodeGroupAddress();
        response.setResults(new GeocodeResultAddress[]{ resultAddress });

        when(restTemplate.getForObject(anyString(), eq(GeocodeGroupAddress.class)))
                .thenReturn(response);

        Optional<String> municipio = service.getMunicipio(40.0, -3.0);

        assertTrue(municipio.isEmpty());
    }

    @Test
    void getMunicipio_nullResponse_returnsEmpty() {
        when(restTemplate.getForObject(anyString(), eq(GeocodeGroupAddress.class)))
                .thenReturn(null);

        Optional<String> municipio = service.getMunicipio(40.0, -3.0);

        assertTrue(municipio.isEmpty());
    }
}
