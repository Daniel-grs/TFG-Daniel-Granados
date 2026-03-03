package es.metrica.sept25.evolutivo.maps.route;

import static org.junit.jupiter.api.Assertions.*;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
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

import es.metrica.sept25.evolutivo.domain.dto.maps.geocode.Geocode;
import es.metrica.sept25.evolutivo.domain.dto.maps.geocode.GeocodeGroup;
import es.metrica.sept25.evolutivo.domain.dto.maps.geocode.GeocodeGroupAddress;
import es.metrica.sept25.evolutivo.domain.dto.maps.geocode.GeocodeResult;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.Coords;
import es.metrica.sept25.evolutivo.service.maps.geocode.GeocodeServiceImpl;

@ExtendWith(MockitoExtension.class)
public class GeocodeServiceImplTest {

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
	        String address = "Madrid";

	        Coords coords = new Coords(40.4168, -3.7038);

	        Geocode geo = new Geocode();
	        geo.setLocation(coords);

	        GeocodeResult result = mock(GeocodeResult.class);
	        when(result.getGeometry()).thenReturn(geo);

	        GeocodeGroup response = mock(GeocodeGroup.class);
	        when(response.getResults()).thenReturn(new GeocodeResult[]{result});

	        when(restTemplate.getForObject(anyString(), eq(GeocodeGroup.class)))
	                .thenReturn(response);

	        Optional<Coords> resultOpt = service.getCoordinates(address);

	        assertTrue(resultOpt.isPresent());
	        assertEquals(40.4168, resultOpt.get().getLat());
	        assertEquals(-3.7038, resultOpt.get().getLng());
	    }

	    @Test
	    void getCoordinates_noResults_returnsEmpty() {
	        GeocodeGroup response = mock(GeocodeGroup.class);
	        when(response.getResults()).thenReturn(new GeocodeResult[]{});

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
}


//import static org.junit.jupiter.api.Assertions.*;
//
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//import java.util.Optional;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.test.util.ReflectionTestUtils;
//import org.springframework.web.client.RestTemplate;
//
//import es.metrica.sept25.evolutivo.domain.dto.maps.geocode.GeocodeGroup;
//import es.metrica.sept25.evolutivo.domain.dto.maps.geocode.GeocodeGroupAddress;
//import es.metrica.sept25.evolutivo.domain.dto.maps.geocode.GeocodeResult;
//import es.metrica.sept25.evolutivo.domain.dto.maps.routes.Coords;
//import es.metrica.sept25.evolutivo.service.maps.geocode.GeocodeServiceImpl;
//
//@ExtendWith(MockitoExtension.class)
//class GeocodeServiceImplTest {
//
//    @Mock
//    private RestTemplate restTemplate;
//
//    @InjectMocks
//    private GeocodeServiceImpl service;
//
//    @BeforeEach
//    void setUp() {
//        ReflectionTestUtils.setField(service, "API_KEY_GOOGLE", "fake-api-key");
//    }
//
//    /* =========================
//     * getCoordinates
//     * ========================= */
//
//    @Test
//    void getCoordinates_success_returnsCoords() {
//        String address = "Madrid";
//
//        Coords coords = new Coords(40.4168, -3.7038);
//
//        GeocodeResult result = mock(GeocodeResult.class);
//        when(result.getGeometry().getLocation()).thenReturn(coords);
//
//        GeocodeGroup response = mock(GeocodeGroup.class);
//        when(response.getResults()).thenReturn(new GeocodeResult[]{result});
//
//        when(restTemplate.getForObject(anyString(), eq(GeocodeGroup.class)))
//                .thenReturn(response);
//
//        Optional<Coords> resultOpt = service.getCoordinates(address);
//
//        assertTrue(resultOpt.isPresent());
//        assertEquals(40.4168, resultOpt.get().getLat());
//        assertEquals(-3.7038, resultOpt.get().getLng());
//    }
//
//    @Test
//    void getCoordinates_noResults_returnsEmpty() {
//        GeocodeGroup response = mock(GeocodeGroup.class);
//        when(response.getResults()).thenReturn(new GeocodeResult[]{});
//
//        when(restTemplate.getForObject(anyString(), eq(GeocodeGroup.class)))
//                .thenReturn(response);
//
//        Optional<Coords> result = service.getCoordinates("Unknown");
//
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    void getCoordinates_nullResponse_returnsEmpty() {
//        when(restTemplate.getForObject(anyString(), eq(GeocodeGroup.class)))
//                .thenReturn(null);
//
//        Optional<Coords> result = service.getCoordinates("Unknown");
//
//        assertTrue(result.isEmpty());
//    }

    /* =========================
     * getMunicipio
     * ========================= */

//    @Test
//    void getMunicipio_withLocality_returnsMunicipio() {
//        AddressComponent component = mock(AddressComponent.class);
//        when(component.getTypes()).thenReturn(List.of("locality"));
//        when(component.getLong_name()).thenReturn("Madrid");
//
//        GeocodeResultAddress result = mock(GeocodeResultAddress.class);
//        when(result.getAddress_components()).thenReturn(List.of(component));
//
//        GeocodeGroupAddress response = mock(GeocodeGroupAddress.class);
//        when(response.getResults()).thenReturn(new GeocodeResultAddress[]{result});
//
//        when(restTemplate.getForObject(anyString(), eq(GeocodeGroupAddress.class)))
//                .thenReturn(response);
//
//        Optional<String> municipio = service.getMunicipio(40.0, -3.0);
//
//        assertTrue(municipio.isPresent());
//        assertEquals("Madrid", municipio.get());
//    }
//
//    @Test
//    void getMunicipio_withAdminLevel4_formatsMunicipio() {
//        AddressComponent component = mock(AddressComponent.class);
//        when(component.getTypes()).thenReturn(List.of("administrative_area_level_4"));
//        when(component.getLong_name()).thenReturn("La Puebla");
//
//        GeocodeResultAddress result = mock(GeocodeResultAddress.class);
//        when(result.getAddress_components()).thenReturn(List.of(component));
//
//        GeocodeGroupAddress response = mock(GeocodeGroupAddress.class);
//        when(response.getResults()).thenReturn(new GeocodeResultAddress[]{result});
//
//        when(restTemplate.getForObject(anyString(), eq(GeocodeGroupAddress.class)))
//                .thenReturn(response);
//
//        Optional<String> municipio = service.getMunicipio(40.0, -3.0);
//
//        assertTrue(municipio.isPresent());
//        assertEquals("Puebla, La", municipio.get());
//    }
//
//    @Test
//    void getMunicipio_noValidTypes_returnsEmpty() {
//        AddressComponent component = mock(AddressComponent.class);
//        when(component.getTypes()).thenReturn(List.of("country"));
//
//        GeocodeResultAddress result = mock(GeocodeResultAddress.class);
//        when(result.getAddress_components()).thenReturn(List.of(component));
//
//        GeocodeGroupAddress response = mock(GeocodeGroupAddress.class);
//        when(response.getResults()).thenReturn(new GeocodeResultAddress[]{result});
//
//        when(restTemplate.getForObject(anyString(), eq(GeocodeGroupAddress.class)))
//                .thenReturn(response);
//
//        Optional<String> municipio = service.getMunicipio(40.0, -3.0);
//
//        assertTrue(municipio.isEmpty());
//    }

//    @Test
//    void getMunicipio_nullResponse_returnsEmpty() {
//        when(restTemplate.getForObject(anyString(), eq(GeocodeGroupAddress.class)))
//                .thenReturn(null);
//
//        Optional<String> municipio = service.getMunicipio(40.0, -3.0);
//
//        assertTrue(municipio.isEmpty());
//    }
//}


