package es.metrica.sept25.evolutivo.controller.weather;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.metrica.sept25.evolutivo.domain.dto.weather.Weather;
import es.metrica.sept25.evolutivo.service.weather.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(
	name = "Clima", 
	description = "Conjunto de endpoints que operan sobre la API de "
				+ "la AEMET para sacar datos del clima en diversas formas."
	)
@RequestMapping("/api/weather")
public class WeatherController {
	
	private static Logger log = LoggerFactory.getLogger(WeatherController.class);

	@Autowired
	private WeatherService weatherService;

	@Operation(
			summary = "Devuelve el clima para un código de zona de AEMET concreto", 
			description = "Compone un objeto Weather que contiene toda la " + 
						  "información meteorológica para un código de zona concreto."
			)
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Clima encontrado"),
			@ApiResponse(responseCode = "400", description = "Bad request: datos malformados"),
			@ApiResponse(responseCode = "404", description = "No se encontró el clima para ese código de AEMET") 
	})
	@GetMapping("/code")
	public ResponseEntity<Weather> getWeather(@RequestParam(required = true) String code) {

		if (code == null || !code.matches("\\d{5}")) {
			log.warn("[weather-controller] [" + LocalDateTime.now().toString() + "] "
					+ "Bad format for INE code inputted");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		Optional<Weather> weather = weatherService.getWeather(code);

		if (weather.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<Weather>(weather.get(), HttpStatus.OK);

	}
}
