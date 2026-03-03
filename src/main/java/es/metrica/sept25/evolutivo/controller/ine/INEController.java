package es.metrica.sept25.evolutivo.controller.ine;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.metrica.sept25.evolutivo.service.ine.INEService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@Tag(name = "INE", description = "Endpoints que se comunican con la lista de municipios "
		+ "y sus códigos en los datos del INE.")
@RequestMapping("/api/ine")
public class INEController {

	@Autowired
	private INEService ineService;

	@Operation(
			summary = "Obtiene el código INE de un municipio",
			description = "Devuelve el `codigoINE` correspondiente a las coordenadas "
					+ "indicadas usando la API del INE (200 - OK). Devuelve 404 si"
					+ "el formato de latitud y longitud son incorrectos o si no hay" 
					+ "código para esas coordenadas."
			)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Código INE encontrado"),
			@ApiResponse(responseCode = "404", description = "Municipio no encontrado")
	})
	@GetMapping("/codigo")
	public ResponseEntity<String> getCodigoINE(
			@RequestParam double lat,
			@RequestParam double lng) {

		Optional<String> codigoINE = ineService.getCodigoINE(lat, lng);
		if (codigoINE.get() == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<>(codigoINE.get(), HttpStatus.OK);
	}
}
