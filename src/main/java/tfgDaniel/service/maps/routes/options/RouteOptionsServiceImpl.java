package tfgDaniel.service.maps.routes.options;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tfgDaniel.domain.dto.user.preferences.EnumOptionDTO;
import tfgDaniel.enums.EmissionType;
import tfgDaniel.enums.FuelType;
import tfgDaniel.enums.MapViewType;
import tfgDaniel.service.traductor.EnumTranslator;



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
