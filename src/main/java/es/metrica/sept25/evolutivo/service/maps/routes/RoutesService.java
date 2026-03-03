package es.metrica.sept25.evolutivo.service.maps.routes;

import java.util.List;
import java.util.Optional;

import org.springframework.web.util.UriComponentsBuilder;

import es.metrica.sept25.evolutivo.domain.dto.maps.routes.Coords;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.CoordsWithWeather;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.RouteGroup;
import es.metrica.sept25.evolutivo.enums.EmissionType;

public interface RoutesService {

	List<Coords> getGasStationsCoordsForRoute(RouteGroup routeGroup, Long radius);

	String getUrl(List<Coords> waypoints, UriComponentsBuilder url);

	List<CoordsWithWeather> getWeatherForRoute(RouteGroup routeGroup);

	List<Coords> extractRoutePoints(RouteGroup routeGroup);

	List<Coords> getLegCoords(RouteGroup routeGroup);
	
	List<Coords> extractRoutePolylinePoints(RouteGroup routeGroup);
	
	List<Coords> decodePolyline(String polylinePoints);

	Optional<RouteGroup> getDirections(String origin, String destination, List<String> waypoints,
			boolean optimizeWaypoints, boolean optimizeRoute, String language, boolean avoidTolls,
			EmissionType vehicleEmissionType);


}
