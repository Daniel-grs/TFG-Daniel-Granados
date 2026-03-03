package es.metrica.sept25.evolutivo.service.traductor;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.metrica.sept25.evolutivo.domain.dto.user.preferences.EnumOptionDTO;
import es.metrica.sept25.evolutivo.entity.user.UserPreferences.Language;
import es.metrica.sept25.evolutivo.entity.user.UserPreferences.Theme;

@Service
public class PreferencesServiceImpl implements PreferencesService{

	@Autowired
	private EnumTranslator translator;


	@Override
	public List<EnumOptionDTO> getLanguages(Locale locale) {
        return Arrays.stream(Language.values())
                .map(lang -> new EnumOptionDTO(
                        lang.name(),
                        translator.translate("language", lang, locale)))
                .toList();
    }

	@Override
    public List<EnumOptionDTO> getThemes(Locale locale) {
        return Arrays.stream(Theme.values())
                .map(theme -> new EnumOptionDTO(
                        theme.name(),
                        translator.translate("theme", theme, locale)))
                .toList();
    }
}
