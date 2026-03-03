package es.metrica.sept25.evolutivo.domain.dto.maps.routes;

import java.util.Map;

public class CoordsWithWeather {

	private String address;
	private Map<Integer, String> weatherDescription;
	private Map<Integer, Double> temperatures;

	public CoordsWithWeather(String address, Map<Integer, String> weatherDescription,
			Map<Integer, Double> temperatures) {
		this.address = address;
		this.weatherDescription = weatherDescription;
		this.temperatures = temperatures;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Map<Integer, String> getWeatherDescription() {
		return weatherDescription;
	}

	public void setWeatherDescription(Map<Integer, String> weatherDescription) {
		this.weatherDescription = weatherDescription;
	}

	public Map<Integer, Double> getTemperatures() {
		return temperatures;
	}

	public void setTemperatures(Map<Integer, Double> temperatures) {
		this.temperatures = temperatures;
	}

}
