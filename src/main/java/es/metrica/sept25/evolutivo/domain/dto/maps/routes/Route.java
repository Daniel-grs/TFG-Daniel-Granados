package es.metrica.sept25.evolutivo.domain.dto.maps.routes;

import java.util.List;

public class Route {

	List<Leg> legs;

	public List<Leg> getLegs() {
		return legs;
	}

	public void setLegs(List<Leg> legs) {
		this.legs = legs;
	}

}
