package es.metrica.sept25.evolutivo.gasolineras;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

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
import es.metrica.sept25.evolutivo.repository.MunicipioRepository;
import es.metrica.sept25.evolutivo.service.gasolineras.MunicipioServiceImpl;
import es.metrica.sept25.evolutivo.service.gasolineras.ProvinciaService;

@ExtendWith(MockitoExtension.class)
public class MunicipioServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ProvinciaService provinciaService;

    @Mock
    private MunicipioRepository municipioRepository;

    @InjectMocks
    private MunicipioServiceImpl service;

    private Provincia prov1;

    @BeforeEach
    void setUp() {
        prov1 = new Provincia();
        prov1.setIdProvincia(1L);
        prov1.setNombreProvincia("Madrid");
    }

    /* =========================
     * getMunicipios
     * ========================= */
    @Test
    void getMunicipios_fromRepository_returnsList() {
        Municipio m1 = new Municipio();
        m1.setIdMunicipio(10L);
        m1.setNombreMunicipio("Madrid");

        when(provinciaService.getProvincias()).thenReturn(List.of(prov1));
        when(municipioRepository.findAll()).thenReturn(List.of(m1));

        List<Municipio> result = service.getMunicipios();

        assertEquals(1, result.size());
        assertEquals("Madrid", result.get(0).getNombreMunicipio());
        verify(restTemplate, never()).getForObject(anyString(), eq(Municipio[].class));
    }

    @Test
    void getMunicipios_repoEmpty_fetchesFromExternalAPI() {
        when(provinciaService.getProvincias()).thenReturn(List.of(prov1));
        when(municipioRepository.findAll()).thenReturn(Collections.emptyList());

        Municipio externalMun = new Municipio();
        externalMun.setIdMunicipio(20L);
        externalMun.setNombreMunicipio("Alcobendas");

        when(restTemplate.getForObject(anyString(), eq(Municipio[].class)))
                .thenReturn(new Municipio[]{externalMun});

        List<Municipio> result = service.getMunicipios();

        assertEquals(1, result.size());
        assertEquals("Alcobendas", result.get(0).getNombreMunicipio());

        verify(municipioRepository).saveAllAndFlush(anyList());
    }

    /* =========================
     * getMunicipioFromId
     * ========================= */
    @Test
    void getMunicipioFromId_found_returnsMunicipio() {
        Municipio m = new Municipio();
        m.setIdMunicipio(10L);
        m.setNombreMunicipio("Madrid");

        when(service.getMunicipios()).thenReturn(List.of(m));

        Optional<Municipio> result = service.getMunicipioFromId(10L);

        assertTrue(result.isPresent());
        assertEquals("Madrid", result.get().getNombreMunicipio());
    }

    @Test
    void getMunicipioFromId_notFound_returnsEmpty() {
        when(service.getMunicipios()).thenReturn(Collections.emptyList());

        Optional<Municipio> result = service.getMunicipioFromId(99L);

        assertTrue(result.isEmpty());
    }

    /* =========================
     * getMunicipioFromString
     * ========================= */
    @Test
    void getMunicipioFromString_found_returnsMunicipio() {
        Municipio m = new Municipio();
        m.setIdMunicipio(10L);
        m.setNombreMunicipio("Madrid");

        when(service.getMunicipios()).thenReturn(List.of(m));

        Optional<Municipio> result = service.getMunicipioFromString("Madrid");

        assertTrue(result.isPresent());
        assertEquals(10L, result.get().getIdMunicipio());
    }

    @Test
    void getMunicipioFromString_caseInsensitive_returnsMunicipio() {
        Municipio m = new Municipio();
        m.setIdMunicipio(10L);
        m.setNombreMunicipio("Madrid");

        when(service.getMunicipios()).thenReturn(List.of(m));

        Optional<Municipio> result = service.getMunicipioFromString("mAdRid");

        assertTrue(result.isPresent());
        assertEquals(10L, result.get().getIdMunicipio());
    }

    @Test
    void getMunicipioFromString_notFound_returnsEmpty() {
        when(service.getMunicipios()).thenReturn(Collections.emptyList());

        Optional<Municipio> result = service.getMunicipioFromString("Unknown");

        assertTrue(result.isEmpty());
    }

}
