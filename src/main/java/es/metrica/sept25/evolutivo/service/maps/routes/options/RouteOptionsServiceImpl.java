package es.metrica.sept25.evolutivo.service.maps.routes.options;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.metrica.sept25.evolutivo.domain.dto.user.preferences.EnumOptionDTO;
import es.metrica.sept25.evolutivo.enums.MapViewType;
import es.metrica.sept25.evolutivo.enums.EmissionType;
import es.metrica.sept25.evolutivo.enums.FuelType;
import es.metrica.sept25.evolutivo.service.traductor.EnumTranslator;

@Service
public class RouteOptionsServiceImpl implements RouteOptionsService{

	@Autowired
	private EnumTranslator translator;

	@Override
	public List<EnumOptionDTO> getFuelTypes(Locale locale) {
        return Arrays.stream(FuelType.values())
                .map(fuel -> new EnumOptionDTO(
                        fuel.name(),
                        translator.translate("fuel", fuel, locale)))
                .toList();
    }
	
	@Override
    public List<EnumOptionDTO> getMapViewTypes(Locale locale) {
        return Arrays.stream(MapViewType.values())
                .map(map -> new EnumOptionDTO(
                        map.name(),
                        translator.translate("map", map, locale)))
                .toList();
    }

	@Override
    public List<EnumOptionDTO> getEmissionTypes(Locale locale) {
        return Arrays.stream(EmissionType.values())
                .map(em -> new EnumOptionDTO(
                        em.name(),
                        translator.translate("emission", em, locale)))
                .toList();
    }

}
