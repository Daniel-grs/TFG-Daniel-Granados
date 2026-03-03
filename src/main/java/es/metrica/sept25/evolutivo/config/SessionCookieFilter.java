package es.metrica.sept25.evolutivo.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import es.metrica.sept25.evolutivo.service.session.CookieService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SessionCookieFilter extends OncePerRequestFilter {

	private final CookieService cookieService;

	public SessionCookieFilter(CookieService cookieService) {
		this.cookieService = cookieService;
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {

		String path = request.getRequestURI();

		return "OPTIONS".equalsIgnoreCase(request.getMethod())
				|| !path.startsWith("/api/")
				|| path.startsWith("/api/routes")
				|| path.startsWith("/api/route")
				|| path.startsWith("/api/oil/gasolineras/radio/coords");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain)
					throws ServletException, IOException {

		Cookie[] cookies = request.getCookies();

		if (cookies == null) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		Optional<Cookie> sessionCookie = Arrays.stream(cookies)
				.filter(c -> "sesionActiva".equals(c.getName()))
				.findFirst();

		if (sessionCookie.isEmpty()
				|| !cookieService.validate(sessionCookie.get().getValue())) {

			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		filterChain.doFilter(request, response);
	}
}

