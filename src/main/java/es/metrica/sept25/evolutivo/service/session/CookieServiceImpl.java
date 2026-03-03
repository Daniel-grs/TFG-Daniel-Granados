package es.metrica.sept25.evolutivo.service.session;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class CookieServiceImpl implements CookieService {

	private static Logger log = LoggerFactory.getLogger(CookieServiceImpl.class);

	@Value("${evolutivo.auth.secret-cookie-key}")
	private String secretString;

	private SecretKeySpec secretKeySpec;

	@Override
	public SecretKeySpec getSecretKey() {
		if (Objects.isNull(secretKeySpec)) {
			secretKeySpec = new SecretKeySpec(secretString.getBytes(StandardCharsets.UTF_8), "AES");
		}
		return secretKeySpec;
	}

	@Override
	public String cipher(String plainText) {
		try {
			// AES-GCM with 12-byte IV and 128-bit tag
			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
			byte[] iv = new byte[12];
			SecureRandom rnd = new SecureRandom();
			rnd.nextBytes(iv);
			GCMParameterSpec spec = new GCMParameterSpec(128, iv);
			cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(), spec);
			byte[] cipherBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

			// prepend IV to cipher bytes
			byte[] out = new byte[iv.length + cipherBytes.length];
			System.arraycopy(iv, 0, out, 0, iv.length);
			System.arraycopy(cipherBytes, 0, out, iv.length, cipherBytes.length);

			return Base64.getEncoder().encodeToString(out);
		} catch (Exception e) {
			log.error("[cookie-service] [" + LocalDateTime.now() + "] " 
					+ "Could not cipher the plain-text String: "
					+ "[" + plainText + "]. Reason: " + e);
			return "";
		}
	}

	@Override
	public String decipher(String cipheredText) {
		try {
			byte[] all = Base64.getDecoder().decode(cipheredText);
			if (all.length < 13) {
				throw new IllegalArgumentException("Ciphertext too short");
			}
			byte[] iv = Arrays.copyOfRange(all, 0, 12);
			byte[] cipherBytes = Arrays.copyOfRange(all, 12, all.length);

			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
			GCMParameterSpec spec = new GCMParameterSpec(128, iv);
			cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec);
			byte[] plain = cipher.doFinal(cipherBytes);
			return new String(plain, StandardCharsets.UTF_8);
		} catch (Exception e) {
			log.error("[cookie-service] [" + LocalDateTime.now() + "] "
					+ "Could not decipher the ciphered text String: " 
					+ "[" + cipheredText + "]. Reason: " + e);
			return "";
		}
	}

	@Override
	public boolean validate(String cipheredCookieValue) {
		if (cipheredCookieValue == null || cipheredCookieValue.isEmpty()) {
			log.error("[cookie-service] [" + LocalDateTime.now() + "] "
					+ "Could not validate the given ciphered cookie: " + 
					"[" + cipheredCookieValue + "].");
			return false;
		}

		try {
			String plainText = decipher(cipheredCookieValue);
			// Regex that verifies the following:
			// - No double separators (// o !!)
			// - No text ending in !
			// - "user!number" repeated format
			String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
			return Objects.nonNull(plainText) && plainText.matches(regex);

		} catch (Exception e) {
			// Failing to decipher or any other exception is considered invalid.
			log.error("[cookie-service] [" + LocalDateTime.now() + "] "
					+ "Failed to validate ciphered cookie value. Reason: " + e);
			return false;
		}
	}

	@Override
	public void createCookie(
			HttpServletResponse response, 
			String name, 
			String value, 
			int maxDurationSeconds
			) {
		String cipheredValue = cipher(value);

		ResponseCookie cookie = ResponseCookie
				.from(name, cipheredValue)
				.path("/")
				.httpOnly(true)
				.secure(true)
				.sameSite("None").maxAge(maxDurationSeconds).build();

		response.addHeader("Set-Cookie", cookie.toString());
		log.info("[cookie-service] [" + LocalDateTime.now() + "] " 
				+ "Created cookie with name: [" + name + "] , and value: " 
				+ "[" + value + "].");
	}

	@Override
	public void deleteCookie(HttpServletResponse response, String name) {
		ResponseCookie cookie = ResponseCookie
				.from(name, "")
				.path("/")
				.httpOnly(true)
				.secure(true)
				.sameSite("None")
				.maxAge(0).build();

		response.addHeader("Set-Cookie", cookie.toString());
		log.info("[cookie-service] [" + LocalDateTime.now() + "] " 
				+ "Deleted cookie with name: [" + name + "].");
	}

	@Override
	public Optional<String> getCookieValue(HttpServletRequest request, String name) {
		if (request.getCookies() == null) {
			log.warn("[cookie-service] [" + LocalDateTime.now() + "] " 
					+ "Couldn't get value from cookie with name: [" + name + "].");
			return Optional.empty();
		}

		Optional<String> transformed = Arrays.stream(request.getCookies())
				.filter(cookie -> cookie.getName().equals(name))
				.findFirst()
				.map(cookie -> decipher(cookie.getValue()));

		if (transformed.isEmpty()) {
			log.warn("[cookie-service] [" + LocalDateTime.now() + "] " 
					+ "Couldn't get value from cookie with name: [" + name + "].");
		} else {
			log.info("[cookie-service] [" + LocalDateTime.now() + "] "
					+ "Retrieved successfully the value from cookie with name: [" + name + "].");
		}

		return transformed;
	}

	@Override
	public void createSessionCookie(HttpServletResponse response, String email) {
		createCookie(response, "sesionActiva", email, 1800); // 30 minutos
		log.info("[cookie-service] [" + LocalDateTime.now() + "] "
				+ "Created the session cookie successfully for email: [" + email + "].");
	}

	@Override
	public void closeSession(HttpServletResponse response, String name) {
		deleteCookie(response, name);
		log.info("[cookie-service] [" + LocalDateTime.now() + "] " 
				+ "Closed the session successfully for email: [" + name + "].");
	}
}
