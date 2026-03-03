package es.metrica.sept25.evolutivo.traductor;

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
import es.metrica.sept25.evolutivo.entity.user.UserPreferences.Language;
import es.metrica.sept25.evolutivo.entity.user.UserPreferences.Theme;
import es.metrica.sept25.evolutivo.service.traductor.EnumTranslator;
import es.metrica.sept25.evolutivo.service.traductor.PreferencesServiceImpl;

@ExtendWith(MockitoExtension.class)
public class PreferencesServiceImplTest {
	 @Mock
	    private EnumTranslator translator;

	    @InjectMocks
	    private PreferencesServiceImpl service;

	    private Locale locale;

	    @BeforeEach
	    void setUp() {
	        locale = Locale.ENGLISH;
	    }

	    @Test
	    void getLanguages_returnsTranslatedLanguages() {
	    	
	        for (Language lang : Language.values()) {
	            when(translator.translate("language", lang, locale))
	                    .thenReturn("Translated-" + lang.name());
	        }

	        List<EnumOptionDTO> result = service.getLanguages(locale);

	        assertEquals(Language.values().length, result.size());

	        for (int i = 0; i < result.size(); i++) {
	            assertEquals(Language.values()[i].name(), result.get(i).getCode());
	            assertEquals("Translated-" + Language.values()[i].name(), result.get(i).getLabel());
	        }

	        for (Language lang : Language.values()) {
	            verify(translator).translate("language", lang, locale);
	        }
	    }

	    @Test
	    void getThemes_returnsTranslatedThemes() {

	    	for (Theme theme : Theme.values()) {
	            when(translator.translate("theme", theme, locale))
	                    .thenReturn("Translated-" + theme.name());
	        }

	        List<EnumOptionDTO> result = service.getThemes(locale);

	        assertEquals(Theme.values().length, result.size());

	        for (int i = 0; i < result.size(); i++) {
	            assertEquals(Theme.values()[i].name(), result.get(i).getCode());
	            assertEquals("Translated-" + Theme.values()[i].name(), result.get(i).getLabel());
	        }

	        for (Theme theme : Theme.values()) {
	            verify(translator).translate("theme", theme, locale);
	        }
	    }

}
