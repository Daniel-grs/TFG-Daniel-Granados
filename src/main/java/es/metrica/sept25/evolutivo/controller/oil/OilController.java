package es.metrica.sept25.evolutivo.controller.oil;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.metrica.sept25.evolutivo.entity.gasolinera.Gasolinera;
import es.metrica.sept25.evolutivo.entity.gasolinera.Municipio;
import es.metrica.sept25.evolutivo.entity.gasolinera.Provincia;
import es.metrica.sept25.evolutivo.service.gasolineras.GasolineraService;
import es.metrica.sept25.evolutivo.service.gasolineras.MunicipioService;
import es.metrica.sept25.evolutivo.service.gasolineras.ProvinciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(
		name = "Gasolineras", 
		description = "Conjunto de endpoints relacionados con la API "
				+ "de Precioil. Los municipios y provincias proporcionados "
				+ "provienen también de dicha API."
		)
@RequestMapping("/api/oil")
public class OilController {

	@Autowired
	private ProvinciaService provinciaService;

	@Autowired
	private MunicipioService municipioService;

	@Autowired
	private GasolineraService gasolineraService;

	@Operation(summary = "Devuelve una lista de las provincias españolas", description = "")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Provincias recuperadas"),
			@ApiResponse(responseCode = "404", description = "No se encontraron provincias") 
	})
	@GetMapping("/provincias")
	public ResponseEntity<List<Provincia>> getProvincias() {
		List<Provincia> list = provinciaService.getProvincias();

		if (list.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<List<Provincia>>(list, HttpStatus.OK);
	}

	@Operation(summary = "Devuelve una mega-lista de municipios con el ID de provincia asociada", description = "Query a ejecutar una vez por base de datos. Se auto-cachea.")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Provincias recuperadas"),
			@ApiResponse(responseCode = "404", description = "No se encontraron provincias") 
	})
	@GetMapping("/municipios")
	public ResponseEntity<List<Municipio>> getMunicipios() {
		List<Municipio> list = municipioService.getMunicipios();

		if (list.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<List<Municipio>>(list, HttpStatus.OK);
	}

	@Operation(
			summary = "Devuelve una lista de gasolineras dado el nombre de su municipio",
			description = "")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Gasolinera encontrada"), 
			@ApiResponse(responseCode = "204", description = "No se encontró la gasolinera indicada") })
	@GetMapping("/gasolineras/{municipio}")
	public ResponseEntity<List<Gasolinera>> getGasolinerasForMunicipio(@PathVariable String municipio) {
		List<Gasolinera> gasolinera = gasolineraService.getGasolinerasForMunicipio(municipio);

		if (gasolinera.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}

		return new ResponseEntity<List<Gasolinera>>(gasolinera, HttpStatus.OK);
	}

	@Operation(summary = "Devuelve una gasolinera dado un ID", description = "")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Gasolinera encontrada"),
			@ApiResponse(responseCode = "404", description = "No se encontró la gasolinera indicada") 
	})
	@GetMapping("/id/{idEstacion}")
	public ResponseEntity<Gasolinera> getGasolineraForId(@PathVariable Long idEstacion) {
		Optional<Gasolinera> gasolinera = gasolineraService.getGasolineraForId(idEstacion);

		if (gasolinera.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<Gasolinera>(gasolinera.get(), HttpStatus.OK);
	}

	@Operation(
			summary = "Devuelve una lista de gasolineras en un cierto radio de una dirección",
			description = "Transforma la dirección en un set de coordenadas mediante la API de "
					+ 		"Geocoding de Google y luego las pasa, junto con el radio, a la "
					+ 		"API de Precioil."
			)
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Gasolineras encontradas"), 
			@ApiResponse(responseCode = "204", description = "No se encontraron gasolineras en ese radio") })
	@GetMapping("/gasolineras/radio/address")
	public ResponseEntity<List<Gasolinera>> getGasolinerasInRadiusAddress(
			@RequestParam(required = true, defaultValue = "Calle Gran Via 1 Madrid Madrid") String direccion,
			@RequestParam(required = true, defaultValue = "5") Long radio) {
		List<Gasolinera> gasolinera = gasolineraService.getGasolinerasInRadiusAddress(direccion, radio);

		if (gasolinera.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}

		return new ResponseEntity<List<Gasolinera>>(gasolinera, HttpStatus.OK);
	}

	@Operation(
			summary = "Devuelve una lista de gasolineras en un cierto radio de unas coordenadas",
			description = "Ejecuta una llamada a la API de Precioil con las coordenadas y el radio"
					+ "dadas"
			)
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Gasolineras encontradas"), 
			@ApiResponse(responseCode = "404", description = "No se encontraron gasolineras en ese radio") })
	@GetMapping("/gasolineras/radio/coords")
	public ResponseEntity<List<Gasolinera>> getGasolinerasInRadius(
			@RequestParam(required = true, defaultValue = "40.4167279") Double latitud, 
			@RequestParam(required = true, defaultValue = "-3.7032905") Double longitud, 
			@RequestParam(required = true, defaultValue = "5") Long radio) {
		List<Gasolinera> gasolinera = gasolineraService.getGasolinerasInRadiusCoords(latitud, longitud, radio);

		if (gasolinera.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<List<Gasolinera>>(gasolinera, HttpStatus.OK);
	}

	@Operation(
			summary = "Devuelve todas las marcas de gasolineras disponibles",
			description = "Obtiene un listado único de marcas de gasolineras a partir de todas las estaciones disponibles. "
					+ "El resultado se devuelve sin duplicados y se sirve desde caché."
			)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Marcas recuperadas correctamente"),
			@ApiResponse(responseCode = "204", description = "No se encontraron marcas")
	})
	@GetMapping("/gasolineras/marcas")
	public ResponseEntity<List<String>> getMarcasGasolineras() {

		List<String> marcas = gasolineraService.getMarcasFromAllGasolineras();

		if (marcas == null || marcas.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}

		return new ResponseEntity<>(marcas, HttpStatus.OK);
	}
}
