package tfgDaniel.service.maps.geocode;

import java.util.Optional;

import tfgDaniel.domain.dto.maps.routes.Coords;


public interface GeocodeService {
	Optional<Coords> getCoordinates(String address);
	Optional<String> getMunicipio(double lat, double lng);
}
