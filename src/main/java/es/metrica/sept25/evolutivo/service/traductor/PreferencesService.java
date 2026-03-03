package es.metrica.sept25.evolutivo.service.traductor;

import java.util.List;
import java.util.Locale;

import es.metrica.sept25.evolutivo.domain.dto.user.preferences.EnumOptionDTO;

public interface PreferencesService {

	List<EnumOptionDTO> getLanguages(Locale locale);

	List<EnumOptionDTO> getThemes(Locale locale);

}
