package tfgDaniel.service.maps.routes;

import java.util.List;
import java.util.Optional;

import org.springframework.web.util.UriComponentsBuilder;

import tfgDaniel.domain.dto.maps.routes.Coords;
import tfgDaniel.domain.dto.maps.routes.RouteGroup;
import tfgDaniel.domain.dto.maps.routes.RoutePointWeatherDTO;


public interface RoutesService {

	List<Coords> getGasStationsCoordsForRoute(RouteGroup routeGroup, Long radius);

//	String getUrl(List<Coords> waypoints, UriComponentsBuilder url);
	String getUrl(List<Coords> waypoints, UriComponentsBuilder url, boolean shouldOptimize);

	List<RoutePointWeatherDTO> getWeatherForRoute(RouteGroup routeGroup);

	List<Coords> extractRoutePoints(RouteGroup routeGroup);

	List<Coords> getLegCoords(RouteGroup routeGroup);
	
	List<Coords> extractRoutePolylinePoints(RouteGroup routeGroup);
	
	List<Coords> decodePolyline(String polylinePoints);

	Optional<RouteGroup> getDirections(String origin, String destination, List<String> waypoints,
			boolean optimizeWaypoints, boolean optimizeRoute, String language, boolean avoidTolls);


}
