package es.metrica.sept25.evolutivo.gasolineras;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import es.metrica.sept25.evolutivo.entity.gasolinera.Municipio;
import es.metrica.sept25.evolutivo.entity.gasolinera.Provincia;
import es.metrica.sept25.evolutivo.repository.ProvinciaRepository;
import es.metrica.sept25.evolutivo.service.gasolineras.ProvinciaServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ProvinciaServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ProvinciaRepository provinciaRepository;

    @InjectMocks
    private ProvinciaServiceImpl service;

    private Provincia prov1;

    @BeforeEach
    void setUp() {
        prov1 = new Provincia();
        prov1.setIdProvincia(1L);
        prov1.setNombreProvincia("Madrid");
    }

    @Test
    void save_province_callsRepositorySave() {
        Provincia provincia = new Provincia();
        provincia.setIdProvincia(1L);
        provincia.setNombreProvincia("Madrid");

        service.save(provincia);

        verify(provinciaRepository).save(provincia);
    }
    
    @Test
    void getProvincias_fromRepository_returnsList() {
        when(provinciaRepository.findAll()).thenReturn(List.of(prov1));

        List<Provincia> result = service.getProvincias();

        assertEquals(1, result.size());
        assertEquals("Madrid", result.get(0).getNombreProvincia());
        verify(restTemplate, never()).getForObject(anyString(), eq(Provincia[].class));
    }

    @Test
    void getProvincias_repoEmpty_fetchesFromExternalAPI() {
        when(provinciaRepository.findAll()).thenReturn(Collections.emptyList());

        Provincia externalProv = new Provincia();
        externalProv.setIdProvincia(10L);
        externalProv.setNombreProvincia("Barcelona");

        when(restTemplate.getForObject(anyString(), eq(Provincia[].class)))
                .thenReturn(new Provincia[]{ externalProv });

        List<Provincia> result = service.getProvincias();

        assertEquals(1, result.size());
        assertEquals("Barcelona", result.get(0).getNombreProvincia());

        verify(provinciaRepository).saveAllAndFlush(anyList());
    }

    @Test
    void getProvincias_nullFromExternalAPI_returnsEmptyList() {
        when(provinciaRepository.findAll()).thenReturn(Collections.emptyList());
        when(restTemplate.getForObject(anyString(), eq(Provincia[].class)))
                .thenReturn(null);

        List<Provincia> result = service.getProvincias();

        assertTrue(result.isEmpty());
        verify(provinciaRepository, never()).saveAllAndFlush(anyList());
    }

    @Test
    void getProvinciaById_found_returnsProvincia() {
        when(provinciaRepository.findById(1L)).thenReturn(Optional.of(prov1));

        Optional<Provincia> result = service.getProvinciaById(1L);

        assertTrue(result.isPresent());
        assertEquals("Madrid", result.get().getNombreProvincia());
    }

    @Test
    void getProvinciaById_notFound_returnsEmpty() {
        when(provinciaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Provincia> result = service.getProvinciaById(99L);

        assertTrue(result.isEmpty());
    }

    @Test
    void getProvinciaForMunicipio_found_returnsProvincia() {
        Municipio mun = new Municipio();
        mun.setIdProvincia(1L);

        when(provinciaRepository.findAll()).thenReturn(List.of(prov1));

        Optional<Provincia> result = service.getProvinciaForMunicipio(mun);

        assertTrue(result.isPresent());
        assertEquals("Madrid", result.get().getNombreProvincia());
    }

    @Test
    void getProvinciaForMunicipio_notFound_returnsEmpty() {
        Municipio mun = new Municipio();
        mun.setIdProvincia(99L);

        when(provinciaRepository.findAll()).thenReturn(List.of(prov1));

        Optional<Provincia> result = service.getProvinciaForMunicipio(mun);

        assertTrue(result.isEmpty());
    }
}
