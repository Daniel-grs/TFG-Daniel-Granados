package tfgDaniel.domain.dto.maps.routes;

import java.util.LinkedList;
import java.util.List;

import tfgDaniel.entity.gasolinera.Gasolinera;


public class CoordsWithStations {
	private List<Coords> coordsList;
	private List<Gasolinera> stations;

	public CoordsWithStations(List<Coords> coordsList, List<Gasolinera> stations) {
		this.coordsList = coordsList;
		this.stations = new LinkedList<Gasolinera>(stations);
	}

	public List<Coords> getCoordsList() {
		return new LinkedList<Coords>(this.coordsList);
	}

	public List<Gasolinera> getStations() {
		return new LinkedList<Gasolinera>(this.stations);
	}
}
