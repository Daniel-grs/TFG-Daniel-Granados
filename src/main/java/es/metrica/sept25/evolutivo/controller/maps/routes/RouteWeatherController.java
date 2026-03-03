package es.metrica.sept25.evolutivo.controller.maps.routes;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.metrica.sept25.evolutivo.domain.dto.maps.routes.CoordsWithWeather;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.RouteGroup;
import es.metrica.sept25.evolutivo.enums.EmissionType;
import es.metrica.sept25.evolutivo.service.maps.routes.RoutesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Direcciones")
public class RouteWeatherController {

	@Autowired
	private RoutesService routesService;


	@Operation(
			summary = "Lista de coordenadas y el clima para cada set de coordenadas (los pasos de una ruta).", 
			description = "Devuelve una lista de coordenadas para cada uno de los pasos de la ruta dada, "
					+ "con una lista del clima por horas para cada punto.")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Pasos y clima encontrados para la ruta dada."),
			@ApiResponse(responseCode = "400", description = "Solicitud errónea: no se pudieron calcular los pasos de la ruta.")
	})
	@GetMapping("/api/routes/weather")
	public ResponseEntity<List<CoordsWithWeather>> getRouteWeather(
			@RequestParam(required = true, defaultValue = "El Vellon") String origin,
			@RequestParam(required = true, defaultValue = "El Molar") String destination,
			@RequestParam(required = false, defaultValue = "") List<String> waypoints,
			@RequestParam(required = false, defaultValue = "false") boolean optimizeWaypoints,
			@RequestParam(required = false, defaultValue = "false") boolean optimizeRoute,
			@RequestParam(required = false, defaultValue = "es") String language,
			@RequestParam(required = false, defaultValue = "false") boolean avoidTolls,
			@RequestParam(required = false, defaultValue = "C")EmissionType vehicleEmissionType
			) {

		Optional<RouteGroup> routeGroupOpt = routesService.getDirections(origin, destination, waypoints, optimizeWaypoints, optimizeRoute, language, avoidTolls, vehicleEmissionType);

		if (routeGroupOpt.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		List<CoordsWithWeather> stepsWithWeather = routesService.getWeatherForRoute(routeGroupOpt.get());
		return new ResponseEntity<>(stepsWithWeather, HttpStatus.OK);
	}
}
