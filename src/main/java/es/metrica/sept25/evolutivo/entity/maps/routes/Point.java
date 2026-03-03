package es.metrica.sept25.evolutivo.entity.maps.routes;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Point {

	public enum TypePoint {
		ORIGIN, WAYPOINT, DESTINATION
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private TypePoint type;

	private String address;

	@ManyToOne
	@JsonBackReference("route-puntos")
	private SavedRoute savedRoute;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TypePoint getType() {
		return type;
	}

	public void setType(TypePoint type) {
		this.type = type;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public SavedRoute getSavedRoute() {
		return savedRoute;
	}

	public void setSavedRoute(SavedRoute savedRoute) {
		this.savedRoute = savedRoute;
	}
}
