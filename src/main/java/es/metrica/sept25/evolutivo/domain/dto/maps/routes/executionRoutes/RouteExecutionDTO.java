package es.metrica.sept25.evolutivo.domain.dto.maps.routes.executionRoutes;

import java.util.List;

public class RouteExecutionDTO {

	private List<String> polylines;
    private long distanceMeters;
    private long durationSeconds;
	public List<String> getPolylines() {
		return polylines;
	}
	public void setPolylines(List<String> polylines) {
		this.polylines = polylines;
	}
	public long getDistanceMeters() {
		return distanceMeters;
	}
	public void setDistanceMeters(Long distanceMeters) {
		this.distanceMeters = distanceMeters;
	}
	public long getDurationSeconds() {
		return durationSeconds;
	}
	public void setDurationSeconds(Long durationSeconds) {
		this.durationSeconds = durationSeconds;
	}
    
    
}
