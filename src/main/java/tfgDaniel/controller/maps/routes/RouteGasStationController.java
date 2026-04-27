package tfgDaniel.controller.maps.routes;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import tfgDaniel.domain.dto.maps.routes.Coords;
import tfgDaniel.domain.dto.maps.routes.RouteGroup;
import tfgDaniel.enums.EmissionType;
import tfgDaniel.service.maps.routes.RoutesService;

@RestController
@Tag(name = "Direcciones")
public class RouteGasStationController {

	@Autowired
	private RoutesService routesService;

	@Operation(
			summary = "Lista de coordenadas de las gasolineras en un radio de los pasos de una ruta",
			description = "Devuelve una lista de coordenadas para cada uno de las gasolineras encontradas"
					+ "un radio de cada punto de la ruta.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Pasos encontrados para la ruta dada."),
			@ApiResponse(responseCode = "400", description = "Solicitud errónea: no se pudieron calcular los pasos de la ruta.")
	})
	@GetMapping("/api/routes/gasStations")
	public ResponseEntity<List<Coords>> getGasolineras(
			@Parameter(example = "El Vellon") @RequestParam(required = true) String origin,
			@Parameter(example = "El Molar") @RequestParam(required = true) String destination,
			@RequestParam(required = false, defaultValue = "") List<String> waypoints,
			@RequestParam(required = false, defaultValue = "false") boolean optimizeWaypoints,
			@RequestParam(required = false, defaultValue = "false") boolean optimizeRoute,
			@RequestParam(required = false, defaultValue = "es") String language,
			@Parameter(example = "5") @RequestParam(required = true) Long radius,
			@RequestParam(required = false, defaultValue = "false") boolean avoidTolls
			) {

		Optional<RouteGroup> routeGroupOpt = routesService.getDirections(origin, destination, waypoints, optimizeWaypoints, optimizeRoute, language, avoidTolls);

		if (routeGroupOpt.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		List<Coords> coordsForStations = routesService.getGasStationsCoordsForRoute(routeGroupOpt.get(), radius);

		return new ResponseEntity<>(coordsForStations, HttpStatus.OK);
	}
}
