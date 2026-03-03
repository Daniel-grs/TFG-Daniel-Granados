package es.metrica.sept25.evolutivo.service.maps.routes.options;

import java.util.List;
import java.util.Locale;

import es.metrica.sept25.evolutivo.domain.dto.user.preferences.EnumOptionDTO;

public interface RouteOptionsService {

	List<EnumOptionDTO> getFuelTypes(Locale locale);

	List<EnumOptionDTO> getMapViewTypes(Locale locale);

	List<EnumOptionDTO> getEmissionTypes(Locale locale);

}
