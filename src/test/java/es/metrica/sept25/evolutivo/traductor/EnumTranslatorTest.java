package es.metrica.sept25.evolutivo.traductor;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import es.metrica.sept25.evolutivo.service.traductor.EnumTranslator;

@ExtendWith(MockitoExtension.class)
public class EnumTranslatorTest {

	@Mock
    private MessageSource messageSource;

    @InjectMocks
    private EnumTranslator translator;
    
    enum TestEnum {
        VALUE_ONE,
        VALUE_TWO
    }
    
    @Test
    void translate_returnsTranslatedMessage() {
        Locale locale = Locale.ENGLISH;

        when(messageSource.getMessage("fuel.value_one", null, locale)).thenReturn("Fuel One");
        when(messageSource.getMessage("fuel.value_two", null, locale)).thenReturn("Fuel Two");

        String result1 = translator.translate("fuel", TestEnum.VALUE_ONE, locale);
        String result2 = translator.translate("fuel", TestEnum.VALUE_TWO, locale);

        assertEquals("Fuel One", result1);
        assertEquals("Fuel Two", result2);

        verify(messageSource).getMessage("fuel.value_one", null, locale);
        verify(messageSource).getMessage("fuel.value_two", null, locale);
    }

    @Test
    void translate_usesLowerCaseEnumName() {
        Locale locale = Locale.ENGLISH;

        when(messageSource.getMessage("prefix.value_one", null, locale)).thenReturn("Translated");

        String result = translator.translate("prefix", TestEnum.VALUE_ONE, locale);

        assertEquals("Translated", result);
        verify(messageSource).getMessage("prefix.value_one", null, locale);
    }
}
