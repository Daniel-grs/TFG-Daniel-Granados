package tfgDaniel.service.weather;

import java.util.Optional;

import tfgDaniel.domain.dto.weather.Weather;


public interface WeatherService {
	public Optional<Weather> getWeather(String code);
}
