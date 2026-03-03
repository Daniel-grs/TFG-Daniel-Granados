package es.metrica.sept25.evolutivo.controller.maps.geocode;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.metrica.sept25.evolutivo.domain.dto.maps.routes.Coords;
import es.metrica.sept25.evolutivo.service.maps.geocode.GeocodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Geocode", description = "Conjunto de endpoints que emplean la API "
								   + "de Geocoding de Google Maps para hacer "
		                           + "traslaciones de coordenadas a direcciones "
		                           + "y viceversa.")
@RequestMapping("/api/geocode")
public class GeocodeController {

	@Autowired
	private GeocodeService geocodeService;

	@Operation(
			summary = "Obtiene las coordenadas de una dirección", 
			description = "Devuelve las coordenadas (latitud y longitud) de la ubicación "
					    + "proporcionada usando la API de Google Geocoding."
			)
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Coordenadas encontradas"),
			@ApiResponse(responseCode = "404", description = "Solicitud incorrecta: no se pudo efectuar la traslación")
	})
	@GetMapping("/normal")
	public ResponseEntity<Coords> getCoordinates(@RequestParam String address) {

		Optional<Coords> coords = geocodeService.getCoordinates(address);
		if (coords.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(coords.get(), HttpStatus.OK);
	}
	
	@Operation(
			summary = "Obtiene el municipio de una dirección", 
			description = "Devuelve el municipio del las coordenadas indicadas usando la "
					    + "API de Google Geocoding."
			)
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Coordenadas encontradas"),
			@ApiResponse(responseCode = "404", description = "Solicitud incorrecta: no se pudo efectuar la traslación")
	})
	@GetMapping("/municipio")
	public ResponseEntity<String> getMunicipio(@RequestParam double lat, @RequestParam double lng) {

		Optional<String> municipio = geocodeService.getMunicipio(lat, lng);

		if (municipio == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(municipio.get(), HttpStatus.OK);
	}
}
