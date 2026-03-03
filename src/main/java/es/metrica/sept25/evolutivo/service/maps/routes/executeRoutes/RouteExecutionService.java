package es.metrica.sept25.evolutivo.service.maps.routes.executeRoutes;

import java.util.Optional;

import es.metrica.sept25.evolutivo.domain.dto.maps.routes.executionRoutes.RouteExecutionDTO;

public interface RouteExecutionService {

	public Optional<RouteExecutionDTO> executeSavedRoute(Long id);
}
