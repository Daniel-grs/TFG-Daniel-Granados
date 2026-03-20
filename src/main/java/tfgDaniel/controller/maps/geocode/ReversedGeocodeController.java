package tfgDaniel.controller.maps.geocode;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import tfgDaniel.service.maps.geocode.ReverseGeocodeService;

@RestController
@Tag(name = "Geocode")
@RequestMapping("/api/geocode")
public class ReversedGeocodeController {

	@Autowired
	private ReverseGeocodeService reverseGeocodeService;

	@Operation(
			summary = "Obtiene la dirección de unas cordenadas", 
			description = "Devuelve el `formatted_address` (formato estándar de Google) "
					    + "correspondiente a la latitud y longitud indicadas usando "
					    + "la API de Google de Geocoding.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Dirección encontrada"),
			@ApiResponse(responseCode = "404", description = "Solicitud incorrecta: no se pudo efectuar la traslación")
	})
	@GetMapping("/reverse")
	public ResponseEntity<String> getAddress(@RequestParam double lat, @RequestParam double lng) {

		Optional<String> address = reverseGeocodeService.getAddress(lat, lng);
		if (address.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(address.get(), HttpStatus.OK);
	}
}
