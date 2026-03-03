package es.metrica.sept25.evolutivo.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import es.metrica.sept25.evolutivo.service.session.CookieServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class CookieServiceImplTest {

    private CookieServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CookieServiceImpl();
        ReflectionTestUtils.setField(service, "secretString", "1234567890123456");
    }

    @Test
    void cipherAndDecipher_shouldReturnOriginalText() {
        String plainText = "test@example.com";

        String ciphered = service.cipher(plainText);
        assertNotNull(ciphered);
        assertNotEquals(plainText, ciphered);

        String deciphered = service.decipher(ciphered);
        assertEquals(plainText, deciphered);
    }

    @Test
    void decipher_invalidCipher_shouldReturnEmpty() {
        String result = service.decipher("invalidciphertext");
        assertEquals("", result);
    }

    @Test
    void validate_validEmail_shouldReturnTrue() {
        String email = "user@example.com";
        String ciphered = service.cipher(email);

        boolean valid = service.validate(ciphered);
        assertTrue(valid);
    }

    @Test
    void validate_invalidEmail_shouldReturnFalse() {
        String invalid = service.cipher("not-an-email");

        boolean valid = service.validate(invalid);
        assertFalse(valid);
    }

    @Test
    void validate_nullOrEmpty_shouldReturnFalse() {
        assertFalse(service.validate(null));
        assertFalse(service.validate(""));
    }

    @Test
    void createCookie_shouldAddHeader() {
        HttpServletResponse response = mock(HttpServletResponse.class);

        service.createCookie(response, "myCookie", "test@example.com", 3600);

        verify(response).addHeader(eq("Set-Cookie"), contains("myCookie"));
    }

    @Test
    void deleteCookie_shouldAddHeaderWithMaxAge0() {
        HttpServletResponse response = mock(HttpServletResponse.class);

        service.deleteCookie(response, "myCookie");

        verify(response).addHeader(eq("Set-Cookie"), contains("Max-Age=0"));
        verify(response).addHeader(eq("Set-Cookie"), contains("myCookie"));
    }

    @Test
    void getCookieValue_existingCookie_shouldReturnDecipheredValue() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie cookie = new Cookie("testCookie", service.cipher("hello@world.com"));
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        Optional<String> value = service.getCookieValue(request, "testCookie");

        assertTrue(value.isPresent());
        assertEquals("hello@world.com", value.get());
    }

    @Test
    void getCookieValue_missingCookie_shouldReturnEmpty() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(null);

        Optional<String> value = service.getCookieValue(request, "anyCookie");
        assertTrue(value.isEmpty());
    }

    @Test
    void createSessionCookie_shouldCallCreateCookie() {
        HttpServletResponse response = mock(HttpServletResponse.class);

        service.createSessionCookie(response, "user@example.com");

        verify(response).addHeader(eq("Set-Cookie"), contains("sesionActiva"));
    }

    @Test
    void closeSession_shouldCallDeleteCookie() {
        HttpServletResponse response = mock(HttpServletResponse.class);

        service.closeSession(response, "sesionActiva");

        verify(response).addHeader(eq("Set-Cookie"), contains("sesionActiva"));
    }
}
