package es.metrica.sept25.evolutivo.domain.dto.maps.geocode;

import es.metrica.sept25.evolutivo.domain.dto.maps.routes.Coords;

public class Geocode {

	private Coords location;

	public Coords getLocation() {
		return location;
	}

	public void setLocation(Coords location) {
		this.location = location;
	}
}
