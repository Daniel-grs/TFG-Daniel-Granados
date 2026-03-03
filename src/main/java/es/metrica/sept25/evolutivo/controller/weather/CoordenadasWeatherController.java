package es.metrica.sept25.evolutivo.controller.weather;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.metrica.sept25.evolutivo.domain.dto.weather.Weather;
import es.metrica.sept25.evolutivo.service.ine.INEService;
import es.metrica.sept25.evolutivo.service.weather.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Clima")
@RequestMapping("/api/weather")
public class CoordenadasWeatherController {

	@Autowired
	private WeatherService weatherService;

	@Autowired
	private INEService ineService;

	@Operation(summary = "Devuelve el clima para unas coordenadas", 
			description = "Compone un objeto Weather que contiene toda la "
					+ "información meteorológica para unas coordenas concretas.")
	@ApiResponses(value = { 
			@ApiResponse(
					responseCode = "200", 
					description = "Clima encontrado"
					),
			@ApiResponse(
					responseCode = "400", 
					description = "Bad request: datos malformados"
					),
			@ApiResponse(
					responseCode = "404",
					description = "No se encontró el clima para esas coordenadas"
					) 
	})
	@GetMapping("/coords")
	public ResponseEntity<Weather> getWeatherByCoords(
			@RequestParam double lat,
			@RequestParam double lng) {

		Optional<String> codigoINE = ineService.getCodigoINE(lat, lng);
		if (codigoINE.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		Optional<Weather> weather = weatherService.getWeather(codigoINE.get());
		if (weather.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<Weather>(weather.get(), HttpStatus.OK);
	}
}
