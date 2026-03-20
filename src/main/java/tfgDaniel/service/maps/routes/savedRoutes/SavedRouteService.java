package tfgDaniel.service.maps.routes.savedRoutes;

import java.util.List;
import java.util.Optional;

import tfgDaniel.domain.dto.maps.routes.savedRoutes.PointDTO;
import tfgDaniel.domain.dto.maps.routes.savedRoutes.SavedRouteDTO;
import tfgDaniel.entity.user.User;
import tfgDaniel.enums.EmissionType;



public interface SavedRouteService {

	Optional<SavedRouteDTO> getSavedRoute(Long id);

	void deleteRoute(Long id, User user);

	Optional<List<SavedRouteDTO>> getAllSavedRoutes(String email);

	SavedRouteDTO saveRoute(String name, List<PointDTO> puntosDTO, User user, boolean optimizeWaypoints,
			boolean optimizeRoute, String language, boolean avoidTolls, EmissionType vehicleEmissionType);
	
	SavedRouteDTO renameRoute(String name, SavedRouteDTO savedRoute);
}
