package es.metrica.sept25.evolutivo.domain.dto.maps.routes.savedRoutes;

import java.util.List;

public class SavedRouteDTO {

	private Long routeId;
	private String name;
	private List<PointDTO> points;
	private RoutePreferencesDTO preferences;

	public Long getRouteId() {
		return routeId;
	}

	public void setRouteId(Long routeId) {
		this.routeId = routeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<PointDTO> getPoints() {
		return points;
	}

	public void setPoints(List<PointDTO> points) {
		this.points = points;
	}

	public RoutePreferencesDTO getPreferences() {
		return preferences;
	}

	public void setPreferences(RoutePreferencesDTO preferences) {
		this.preferences = preferences;
	}
}
