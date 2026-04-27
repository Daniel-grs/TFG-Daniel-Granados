package tfgDaniel.service.maps.routes.executeRoutes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import tfgDaniel.domain.dto.maps.routes.Leg;
import tfgDaniel.domain.dto.maps.routes.RouteGroup;
import tfgDaniel.domain.dto.maps.routes.executionRoutes.RouteExecutionDTO;
import tfgDaniel.domain.dto.maps.routes.savedRoutes.PointDTO;
import tfgDaniel.domain.dto.maps.routes.savedRoutes.SavedRouteDTO;
import tfgDaniel.enums.EmissionType;
import tfgDaniel.service.maps.routes.RoutesService;
import tfgDaniel.service.maps.routes.savedRoutes.SavedRouteService;

@Service
public class RouteExecutionServiceImpl implements RouteExecutionService{

	private static final Logger log = LoggerFactory.getLogger(RouteExecutionServiceImpl.class);

	@Autowired
	private SavedRouteService savedRouteService;

	@Autowired
	private RoutesService routesService;


	@Override
	@Cacheable("execSavedRoute")
	public Optional<RouteExecutionDTO> executeSavedRoute(Long id) {
		log.info("[route-exec-service] [" + LocalDateTime.now().toString() + "] "
				+ "Attempting to execute saved route with ID: " + id + ".");
		Optional<SavedRouteDTO> savedRouteOpt = savedRouteService.getSavedRoute(id);

		if (savedRouteOpt.isEmpty()) {
			log.warn("[route-exec-service] [" + LocalDateTime.now().toString() + "] "
					+ "Couldn't find a saved route for ID: " + id + ".");
			return Optional.empty();
		}

		SavedRouteDTO savedRoute = savedRouteOpt.get();

		if (savedRoute.getPoints().isEmpty()) {
			log.warn("[route-exec-service] [" + LocalDateTime.now().toString() + "] "
					+ "Fail to execute saved route - "
					+ "The saved route has no points.");
			return Optional.empty();
		}

		PointDTO inicio = savedRoute.getPoints().get(0);
		PointDTO fin = savedRoute.getPoints().get(savedRoute.getPoints().size() - 1);
		List<String> intermedios = savedRoute.getPoints().subList(1, savedRoute.getPoints().size() - 1)
				.stream()
				.map(PointDTO::getAddress)
				.toList();

		Optional<RouteGroup> routeGroupOpt = routesService.getDirections(
				inicio.getAddress(),
				fin.getAddress(),
				intermedios,
				false,
				false,
				"es",
				false
				);

		if (routeGroupOpt.isEmpty()) {
			log.warn("[route-exec-service] [" + LocalDateTime.now().toString() + "] "
					+ "Fail to execute saved route - "
					+ "Could not get directions for the saved route.");
			return Optional.empty();
		}

		RouteGroup routeGroup = routeGroupOpt.get();
		Leg leg = routeGroup.getRoutes().get(0).getLegs().get(0);

		RouteExecutionDTO dto = new RouteExecutionDTO();
		dto.setDistanceMeters(leg.getDistance().getValue());
		dto.setDurationSeconds(leg.getDuration().getValue());

		List<String> polylines = leg.getSteps().stream()
				.map(step -> step.getPolyline().getPoints())
				.toList();

		dto.setPolylines(polylines);

		log.info("[route-exec-service] [" + LocalDateTime.now().toString() + "] "
				+ "Successfully extracted route execution for ID: " + id + ".");
		return Optional.of(dto);
	}
}
