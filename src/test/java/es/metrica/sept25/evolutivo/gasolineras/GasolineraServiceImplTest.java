package es.metrica.sept25.evolutivo.gasolineras;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import es.metrica.sept25.evolutivo.domain.dto.maps.routes.Coords;
import es.metrica.sept25.evolutivo.entity.gasolinera.Brand;
import es.metrica.sept25.evolutivo.entity.gasolinera.Gasolinera;
import es.metrica.sept25.evolutivo.entity.gasolinera.Municipio;
import es.metrica.sept25.evolutivo.enums.BrandEnum;
import es.metrica.sept25.evolutivo.repository.BrandRepository;
import es.metrica.sept25.evolutivo.repository.GasolineraRepository;
import es.metrica.sept25.evolutivo.service.gasolineras.GasolineraServiceImpl;
import es.metrica.sept25.evolutivo.service.gasolineras.MunicipioService;
import es.metrica.sept25.evolutivo.service.maps.geocode.GeocodeService;

@ExtendWith(MockitoExtension.class)
public class GasolineraServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private MunicipioService municipioService;

    @Mock
    private GeocodeService geocodeService;

    @Mock
    private GasolineraRepository gasolineraRepository;

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private GasolineraServiceImpl service;

    @Test
    void getGasolineraForId_success_returnsGasolinera() {
        Gasolinera gas = new Gasolinera();
        gas.setIdEstacion(1L);
        gas.setMarca("Repsol");

        when(restTemplate.getForObject(anyString(), eq(Gasolinera.class))).thenReturn(gas);

        Optional<Gasolinera> result = service.getGasolineraForId(1L);

        assertTrue(result.isPresent());
        assertEquals("Repsol", result.get().getMarca());
    }

    @Test
    void getGasolineraForId_notFound_returnsEmpty() {
        when(restTemplate.getForObject(anyString(), eq(Gasolinera.class)))
                .thenThrow(HttpClientErrorException.NotFound.class);

        Optional<Gasolinera> result = service.getGasolineraForId(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void getGasolinerasForMunicipio_found_returnsList() {
        Municipio mun = new Municipio();
        mun.setIdMunicipio(10L);
        mun.setNombreMunicipio("Madrid");

        when(municipioService.getMunicipioFromString("Madrid")).thenReturn(Optional.of(mun));

        Gasolinera g1 = new Gasolinera();
        g1.setMarca("Repsol");
        Gasolinera g2 = new Gasolinera();
        g2.setMarca("Cepsa");

        when(restTemplate.getForObject(anyString(), eq(Gasolinera[].class)))
                .thenReturn(new Gasolinera[]{g1, g2});

        List<Gasolinera> result = service.getGasolinerasForMunicipio("Madrid");

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(g -> g.getMarca().equals("Repsol")));
    }

    @Test
    void getGasolinerasForMunicipio_notFoundMunicipio_returnsEmpty() {
        when(municipioService.getMunicipioFromString("Unknown")).thenReturn(Optional.empty());

        List<Gasolinera> result = service.getGasolinerasForMunicipio("Unknown");

        assertTrue(result.isEmpty());
    }

    @Test
    void getGasolinerasInRadiusCoords_success_returnsList() {
        Gasolinera g = new Gasolinera();
        g.setMarca("Repsol");

        when(restTemplate.getForObject(anyString(), eq(Gasolinera[].class)))
                .thenReturn(new Gasolinera[]{g});

        List<Gasolinera> result = service.getGasolinerasInRadiusCoords(40.0, -3.0, 10L);

        assertEquals(1, result.size());
        assertEquals("Repsol", result.get(0).getMarca());
    }

    @Test
    void getGasolinerasInRadiusCoords_invalidRadius_returnsEmpty() {
        List<Gasolinera> result = service.getGasolinerasInRadiusCoords(40.0, -3.0, 0L);

        assertTrue(result.isEmpty());
    }

    @Test
    void getGasolinerasInRadiusCoords_notFound_returnsEmpty() {
        when(restTemplate.getForObject(anyString(), eq(Gasolinera[].class)))
                .thenThrow(HttpClientErrorException.NotFound.class);

        List<Gasolinera> result = service.getGasolinerasInRadiusCoords(40.0, -3.0, 10L);

        assertTrue(result.isEmpty());
    }

    @Test
    void getGasolinerasInRadiusAddress_success_returnsList() {
        Coords coords = new Coords(40.0, -3.0);
        when(geocodeService.getCoordinates("Calle Falsa")).thenReturn(Optional.of(coords));

        Gasolinera g = new Gasolinera();
        g.setMarca("Repsol");

        when(restTemplate.getForObject(anyString(), eq(Gasolinera[].class)))
                .thenReturn(new Gasolinera[]{g});

        List<Gasolinera> result = service.getGasolinerasInRadiusAddress("Calle Falsa", 10L);

        assertEquals(1, result.size());
        assertEquals("Repsol", result.get(0).getMarca());
    }

    @Test
    void getGasolinerasInRadiusAddress_noCoords_returnsEmpty() {
        when(geocodeService.getCoordinates("Unknown")).thenReturn(Optional.empty());

        List<Gasolinera> result = service.getGasolinerasInRadiusAddress("Unknown", 10L);

        assertTrue(result.isEmpty());
    }

    @Test
    void getGasolinerasInRadiusAddress_invalidRadius_returnsEmpty() {
        Coords coords = new Coords(40.0, -3.0);
        when(geocodeService.getCoordinates("Calle Falsa")).thenReturn(Optional.of(coords));

        List<Gasolinera> result = service.getGasolinerasInRadiusAddress("Calle Falsa", 0L);

        assertTrue(result.isEmpty());
    }
    
    @Test
    void getMarcasFromAllGasolineras_returnsEnumBrandsSorted() {
        List<String> marcas = service.getMarcasFromAllGasolineras();

        // Verifica que el tamaño coincide con el enum
        assertEquals(BrandEnum.values().length, marcas.size());

        // Verifica que contenga algunas marcas conocidas
        assertTrue(marcas.contains("Repsol"));
        assertTrue(marcas.contains("Cepsa"));

        // Verifica que esté ordenado alfabéticamente
        List<String> sorted = marcas.stream().sorted().toList();
        assertEquals(sorted, marcas);
    }
    



}
