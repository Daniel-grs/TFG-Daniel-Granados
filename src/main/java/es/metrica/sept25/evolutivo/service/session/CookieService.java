package es.metrica.sept25.evolutivo.service.session;

import java.util.Optional;

import javax.crypto.spec.SecretKeySpec;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface CookieService {
	SecretKeySpec getSecretKey();

	String cipher(String plainText);

	String decipher(String cipheredText);

	/**
	 * Validates the format of a ciphered cookie by the business rules. Tries to
	 * decipher it first and then validates the plain-text contents.
	 * 
	 * @param cipheredCookieValue The cookie value pre-deciphering.
	 * @return true if the deciphered value is formatted correctly. False in case it
	 *         isn't or if the deciphering fails.
	 */
	boolean validate(String cipheredCookieValue);

	/**
	 * Creates a new cookie with the basic security configuration.
	 */
	void createCookie(HttpServletResponse response, String name, String value, int maxDurationSeconds);

	/**
	 * Deletes a cookie from the client
	 *
	 * @param response An HttpServletResponse object to manipulate the cookies.
	 * @param name   Name of the cookie to delete.
	 */
	void deleteCookie(HttpServletResponse response, String name);

	/**
	 * Obtains the value of a specific cookie.
	 *
	 * @param request A HttpServletRequest object to get the cookie out of.
	 * @param name  Name of the cookie to find
	 * @return Value of the cookie or an empty Optional if it doesn't exist.
	 */
	Optional<String> getCookieValue(HttpServletRequest request, String name);

	/**
	 * Creates the active session cookie.
	 */
	void createSessionCookie(HttpServletResponse response, String correo);

	/**
	 * Handles the logout cookie deletion.
	 */
	void closeSession(HttpServletResponse response, String nombre);
}
