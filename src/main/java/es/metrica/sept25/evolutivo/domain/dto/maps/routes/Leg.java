package es.metrica.sept25.evolutivo.domain.dto.maps.routes;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Transient;

public class Leg {

	private Distance distance;

	@Transient
	private Duration duration;

	@JsonProperty("end_address")
	private String endAddress;

	@JsonProperty("start_address")
	private String startAddress;

	@JsonProperty("end_location")
	private Coords endLocation;

	@JsonProperty("start_location")
	private Coords startLocation;
	List<Step> steps;

	public Distance getDistance() {
		return distance;
	}

	public void setDistance(Distance distance) {
		this.distance = distance;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public String getEndAddress() {
		return endAddress;
	}

	public void setEndAddress(String endAddress) {
		this.endAddress = endAddress;
	}

	public String getStartAddress() {
		return startAddress;
	}

	public void setStartAddress(String startAddress) {
		this.startAddress = startAddress;
	}

	public Coords getEndLocation() {
		return endLocation;
	}

	public void setEndLocation(Coords endLocation) {
		this.endLocation = endLocation;
	}

	public Coords getStartLocation() {
		return startLocation;
	}

	public void setStartLocation(Coords startLocation) {
		this.startLocation = startLocation;
	}

	public List<Step> getSteps() {
		return steps;
	}

	public void setSteps(List<Step> steps) {
		this.steps = steps;
	}

}
