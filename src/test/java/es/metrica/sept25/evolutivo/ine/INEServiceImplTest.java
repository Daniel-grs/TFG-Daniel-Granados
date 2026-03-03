package es.metrica.sept25.evolutivo.ine;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import es.metrica.sept25.evolutivo.entity.ine.INEMunicipio;
import es.metrica.sept25.evolutivo.repository.INEMunicipioRepository;
import es.metrica.sept25.evolutivo.service.ine.INEServiceImpl;
import es.metrica.sept25.evolutivo.service.maps.geocode.GeocodeService;

@ExtendWith(MockitoExtension.class)
class INEServiceImplTest {

    @Mock
    private GeocodeService geocodeService;

    @Mock
    private INEMunicipioRepository ineMunicipioRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private INEServiceImpl service;

    @BeforeEach
    void setUp() {
        // nada extra por ahora
    }

    /* =========================
     * getCodigoINE
     * ========================= */

    @Test
    void getCodigoINE_municipioNotFound_returnsEmpty() {
        when(geocodeService.getMunicipio(40.0, -3.0)).thenReturn(Optional.empty());

        Optional<String> codigoOpt = service.getCodigoINE(40.0, -3.0);

        assertTrue(codigoOpt.isEmpty());
    }

    @Test
    void getCodigoINE_repoEmpty_fetchesFromExternalAPI() {
        when(geocodeService.getMunicipio(40.0, -3.0)).thenReturn(Optional.of("Madrid"));

        when(ineMunicipioRepository.count()).thenReturn(0L);

        INEMunicipio externalMun = new INEMunicipio();
        externalMun.setNombre("Madrid");
        externalMun.setCodigoINE("28079");

        when(restTemplate.getForObject(anyString(), eq(INEMunicipio[].class)))
                .thenReturn(new INEMunicipio[]{ externalMun });

        when(ineMunicipioRepository.saveAllAndFlush(anyList())).thenReturn(List.of(externalMun));

        when(ineMunicipioRepository.findAll()).thenReturn(List.of(externalMun));

        Optional<String> codigoOpt = service.getCodigoINE(40.0, -3.0);

        assertTrue(codigoOpt.isPresent());
        assertEquals("28079", codigoOpt.get());

        verify(ineMunicipioRepository).saveAllAndFlush(anyList());
        verify(ineMunicipioRepository).findAll();
    }

    @Test
    void getCodigoINE_repoNotEmpty_findsInRepository() {
        when(geocodeService.getMunicipio(40.0, -3.0)).thenReturn(Optional.of("Madrid"));
        when(ineMunicipioRepository.count()).thenReturn(1L);

        INEMunicipio mun = new INEMunicipio();
        mun.setNombre("Madrid");
        mun.setCodigoINE("28079");

        when(ineMunicipioRepository.findAll()).thenReturn(List.of(mun));

        Optional<String> codigoOpt = service.getCodigoINE(40.0, -3.0);

        assertTrue(codigoOpt.isPresent());
        assertEquals("28079", codigoOpt.get());

        verify(restTemplate, never()).getForObject(anyString(), eq(INEMunicipio[].class));
    }

    @Test
    void getCodigoINE_municipioNotInRepo_returnsEmpty() {
        when(geocodeService.getMunicipio(40.0, -3.0)).thenReturn(Optional.of("Barcelona"));
        when(ineMunicipioRepository.count()).thenReturn(1L);

        INEMunicipio mun = new INEMunicipio();
        mun.setNombre("Madrid");
        mun.setCodigoINE("28079");

        when(ineMunicipioRepository.findAll()).thenReturn(List.of(mun));

        Optional<String> codigoOpt = service.getCodigoINE(40.0, -3.0);

        assertTrue(codigoOpt.isEmpty());
    }

    @Test
    void getCodigoINE_externalApiReturnsNull_returnsEmpty() {
        when(geocodeService.getMunicipio(40.0, -3.0)).thenReturn(Optional.of("Madrid"));
        when(ineMunicipioRepository.count()).thenReturn(0L);

        when(restTemplate.getForObject(anyString(), eq(INEMunicipio[].class)))
                .thenReturn(null);

        Optional<String> codigoOpt = service.getCodigoINE(40.0, -3.0);

        assertTrue(codigoOpt.isEmpty());
    }
}

