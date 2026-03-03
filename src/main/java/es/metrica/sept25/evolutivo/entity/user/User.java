package es.metrica.sept25.evolutivo.entity.user;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import es.metrica.sept25.evolutivo.entity.gasolinera.UserSavedGasStation;
import es.metrica.sept25.evolutivo.entity.maps.routes.RoutePreferences;
import es.metrica.sept25.evolutivo.entity.maps.routes.SavedRoute;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "users")
public class User {

	public enum PrioridadGasolineras {
		PRICE, DISTANCE, BOTH
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "email", unique = true)
	private String email;

	private String password;

	@Transient
	private String passwordConfirmation;

	private String name;
	private String surname;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private UserPreferences userPreferences;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference("user-routes")
	private List<SavedRoute> savedRoutes = new LinkedList<SavedRoute>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference("user-gasStations")
	private List<UserSavedGasStation> savedGasStations = new LinkedList<>();

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PrioridadGasolineras gasStationPriority = PrioridadGasolineras.PRICE;

	@Embedded
	private RoutePreferences routePreferences;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordConfirmation() {
		return passwordConfirmation;
	}

	public void setPasswordConfirmation(String passwordConfirmation) {
		this.passwordConfirmation = passwordConfirmation;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public UserPreferences getUserPreferences() {
		return userPreferences;
	}

	public void setUserPreferences(UserPreferences userPreferences) {
		this.userPreferences = userPreferences;
	}

	public List<SavedRoute> getSavedRoutes() {
		return savedRoutes;
	}

	public void setSavedRoutes(List<SavedRoute> savedRoutes) {
		this.savedRoutes = savedRoutes;
	}

	public List<UserSavedGasStation> getSavedGasStations() {
		return savedGasStations;
	}

	public void setSavedGasStations(List<UserSavedGasStation> savedGasStations) {
		this.savedGasStations = savedGasStations;
	}

	public PrioridadGasolineras getGasStationPriority() {
		return gasStationPriority;
	}

	public void setGasStationPriority(PrioridadGasolineras gasStationPriority) {
		this.gasStationPriority = gasStationPriority;
	}

	public RoutePreferences getRoutePreferences() {
		return routePreferences;
	}

	public void setRoutePreferences(RoutePreferences routePreferences) {
		this.routePreferences = routePreferences;
	}

}
