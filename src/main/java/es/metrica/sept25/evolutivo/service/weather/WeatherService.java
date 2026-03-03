package es.metrica.sept25.evolutivo.service.weather;

import java.util.Optional;

import es.metrica.sept25.evolutivo.domain.dto.weather.Weather;

public interface WeatherService {
	public Optional<Weather> getWeather(String code);
}
