package es.metrica.sept25.evolutivo.service.traductor;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class EnumTranslator {

	@Autowired
	private MessageSource messageSource;

    public String translate(String prefix, Enum<?> value, Locale locale) {
        String key = prefix + "." + value.name().toLowerCase();
        return messageSource.getMessage(key, null, locale);
    }
}
