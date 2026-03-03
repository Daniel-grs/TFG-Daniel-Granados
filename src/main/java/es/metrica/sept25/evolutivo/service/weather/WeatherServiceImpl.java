package es.metrica.sept25.evolutivo.service.weather;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpClientErrorException.TooManyRequests;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.metrica.sept25.evolutivo.domain.dto.weather.Dia;
import es.metrica.sept25.evolutivo.domain.dto.weather.Prediccion;
import es.metrica.sept25.evolutivo.domain.dto.weather.Weather;
import es.metrica.sept25.evolutivo.domain.dto.weather.WeatherLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class WeatherServiceImpl implements WeatherService {

	private static final Logger log = LoggerFactory.getLogger(WeatherServiceImpl.class);

	private static final String API_URL = "https://opendata.aemet.es/opendata/api/prediccion/especifica/municipio/horaria/";

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	@Value("${evolutivo.api_key_aemet}")
	private String API_KEY_AEMET;

	@Retryable(
			maxRetries = 60,
			delay = 1000,
			multiplier = 1.5,
			value = HttpClientErrorException.TooManyRequests.class
	)
	@Cacheable(value = "weather", cacheManager = "climateCacheManager")
	public Optional<Weather> getWeather(String code) {
		log.info("[weather-service] [" + LocalDateTime.now().toString() + "] "
				+ "Attempting to get the weather object for a given INE code: " 
				+ code + ".");
		String url = UriComponentsBuilder
				.fromUriString(API_URL)
				.path(code)
				.queryParam("api_key", API_KEY_AEMET)
				.toUriString();

		WeatherLink weather = restTemplate.getForObject(url, WeatherLink.class);
		return getFirstWeatherDay(getWeatherData(weather.getDatos()));
	}

	@Retryable(
			maxRetries = 60, 
			delay = 1000, 
			multiplier = 1.5, 
			value = TooManyRequests.class
	)
	@Qualifier("climateCacheManager")
	@Cacheable(value = "weatherData", cacheManager = "climateCacheManager")
	private List<Weather> getWeatherData(String url) {
		log.info("[weather-service] [" + LocalDateTime.now().toString() + "] "
				+ "Attempting to get Weather data for the given URL: " + url);
		String json = restTemplate.getForObject(url, String.class);
		try {
			log.info("[weather-service] [" + LocalDateTime.now().toString() + "] "
					+ "Successfully converted the JSON obtained from " + url);
			return objectMapper.readValue(json, new TypeReference<List<Weather>>() {
			});
		} catch (JsonMappingException e) {
			log.error("[weather-service] [" + LocalDateTime.now().toString() + "] "
					+ "Failed to map the Weather JSON to the Java object. Stacktrace:");
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			log.error("[weather-service] [" + LocalDateTime.now().toString() + "] "
					+ "Failed to process the Weather JSON for the Java object. Stacktrace:");
			e.printStackTrace();
		}

		log.error("[weather-service] [" + LocalDateTime.now().toString() + "] "
				+ "Couldn't retrieve any weather for the given URL: " + url);
		return Collections.emptyList();
	}

	private Optional<Weather> getFirstWeatherDay(List<Weather> weatherList) {
		if (weatherList.isEmpty())
			return Optional.empty();

		Weather w = weatherList.getFirst();
		Prediccion p = w.getPrediccion();
		Dia firstDay = p.getDia().getFirst();
		p.setDia(new ArrayList<Dia>(Arrays.asList(firstDay)));
		w.setPrediccion(p);
		return Optional.of(w);
	}
}
