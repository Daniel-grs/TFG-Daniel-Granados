package es.metrica.sept25.evolutivo.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Fallback controller for unmapped /api/** endpoints. Specific controllers will
 * take precedence; this returns a centralized 404 for missing API routes.
 */
@Hidden
@RestController
@RequestMapping("/api")
public class ApiFallbackController {

	private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_DATE_TIME;

	@RequestMapping("/**")
	public ResponseEntity<Map<String, Object>> fallback(HttpServletRequest request) {
		Map<String, Object> body = new HashMap<>();
		body.put("timestamp", LocalDateTime.now().format(ISO));
		body.put("status", HttpStatus.NOT_FOUND.value());
		body.put("error", HttpStatus.NOT_FOUND.getReasonPhrase());
		body.put("message", "API endpoint not found");
		body.put("path", request.getRequestURI());
		body.put("method", request.getMethod());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
	}
}
