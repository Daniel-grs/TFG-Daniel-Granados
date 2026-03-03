package es.metrica.sept25.evolutivo.entity.maps.routes;

import java.util.List;

import es.metrica.sept25.evolutivo.enums.EmissionType;
import es.metrica.sept25.evolutivo.enums.FuelType;
import es.metrica.sept25.evolutivo.enums.MapViewType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class RoutePreferences {

	@ElementCollection
	private List<String> preferredBrands;

	private Integer radioKm;

	@Enumerated(EnumType.STRING)
	private FuelType fuelType;

	@Enumerated(EnumType.STRING)
	private EmissionType emissionType;

	private Double maxPrice;

	@Enumerated(EnumType.STRING)
	private MapViewType mapView;

	private boolean avoidTolls;

	public List<String> getPreferredBrands() {
		return preferredBrands;
	}

	public void setPreferredBrands(List<String> preferredBrands) {
		this.preferredBrands = preferredBrands;
	}

	public Integer getRadioKm() {
		return radioKm;
	}

	public void setRadioKm(Integer radioKm) {
		this.radioKm = radioKm;
	}

	public FuelType getFuelType() {
		return fuelType;
	}

	public void setFuelType(FuelType fuelType) {
		this.fuelType = fuelType;
	}

	public EmissionType getEmissionType() {
		return emissionType;
	}

	public void setEmissionType(EmissionType emissionType) {
		this.emissionType = emissionType;
	}

	public Double getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(Double maxPrice) {
		this.maxPrice = maxPrice;
	}

	public MapViewType getMapView() {
		return mapView;
	}

	public void setMapView(MapViewType mapView) {
		this.mapView = mapView;
	}

	public boolean isAvoidTolls() {
		return avoidTolls;
	}

	public void setAvoidTolls(boolean avoidTolls) {
		this.avoidTolls = avoidTolls;
	}

}
