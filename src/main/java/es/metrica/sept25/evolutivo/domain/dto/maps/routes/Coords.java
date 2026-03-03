package es.metrica.sept25.evolutivo.domain.dto.maps.routes;

import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class Coords {

	private Double lat;
	private Double lng;

	public Coords() {
	}

	public Coords(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLng() {
		return lng;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}

	@Override
	public int hashCode() {
		return Objects.hash(lat, lng);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coords other = (Coords) obj;
		return Objects.equals(lat, other.lat) && Objects.equals(lng, other.lng);
	}

	@Override
	public String toString() {
		return lat + "," + lng;
	}
	
}
