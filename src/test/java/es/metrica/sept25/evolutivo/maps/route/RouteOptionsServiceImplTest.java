package es.metrica.sept25.evolutivo.maps.route;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.metrica.sept25.evolutivo.domain.dto.user.preferences.EnumOptionDTO;
import es.metrica.sept25.evolutivo.enums.EmissionType;
import es.metrica.sept25.evolutivo.enums.FuelType;
import es.metrica.sept25.evolutivo.enums.MapViewType;
import es.metrica.sept25.evolutivo.service.maps.routes.options.RouteOptionsServiceImpl;
import es.metrica.sept25.evolutivo.service.traductor.EnumTranslator;

@ExtendWith(MockitoExtension.class)
class RouteOptionsServiceImplTest {

    @Mock
    private EnumTranslator translator;

    @InjectMocks
    private RouteOptionsServiceImpl service;

    private Locale locale;

    @BeforeEach
    void setUp() {
        locale = Locale.ENGLISH;
    }

    @Test
    void getFuelTypes_returnsTranslatedList() {
        // Simulamos la traducción de cada FuelType
        for (FuelType fuel : FuelType.values()) {
            when(translator.translate("fuel", fuel, locale))
                .thenReturn("Translated-" + fuel.name());
        }

        List<EnumOptionDTO> result = service.getFuelTypes(locale);

        assertEquals(FuelType.values().length, result.size());

        for (int i = 0; i < result.size(); i++) {
            assertEquals(FuelType.values()[i].name(), result.get(i).getCode());
            assertEquals("Translated-" + FuelType.values()[i].name(), result.get(i).getLabel());
        }

        for (FuelType fuel : FuelType.values()) {
            verify(translator).translate("fuel", fuel, locale);
        }
    }

    @Test
    void getMapViewTypes_returnsTranslatedList() {
        for (MapViewType map : MapViewType.values()) {
            when(translator.translate("map", map, locale))
                .thenReturn("Translated-" + map.name());
        }

        List<EnumOptionDTO> result = service.getMapViewTypes(locale);

        assertEquals(MapViewType.values().length, result.size());

        for (int i = 0; i < result.size(); i++) {
            assertEquals(MapViewType.values()[i].name(), result.get(i).getCode());
            assertEquals("Translated-" + MapViewType.values()[i].name(), result.get(i).getLabel());
        }

        for (MapViewType map : MapViewType.values()) {
            verify(translator).translate("map", map, locale);
        }
    }

    @Test
    void getEmissionTypes_returnsTranslatedList() {
        for (EmissionType em : EmissionType.values()) {
            when(translator.translate("emission", em, locale))
                .thenReturn("Translated-" + em.name());
        }

        List<EnumOptionDTO> result = service.getEmissionTypes(locale);

        assertEquals(EmissionType.values().length, result.size());

        for (int i = 0; i < result.size(); i++) {
            assertEquals(EmissionType.values()[i].name(), result.get(i).getCode());
            assertEquals("Translated-" + EmissionType.values()[i].name(), result.get(i).getLabel());
        }

        for (EmissionType em : EmissionType.values()) {
            verify(translator).translate("emission", em, locale);
        }
    }
}
