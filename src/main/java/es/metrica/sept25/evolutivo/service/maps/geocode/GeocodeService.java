package es.metrica.sept25.evolutivo.service.maps.geocode;

import java.util.Optional;

import es.metrica.sept25.evolutivo.domain.dto.maps.routes.Coords;

public interface GeocodeService {
	Optional<Coords> getCoordinates(String address);
	Optional<String> getMunicipio(double lat, double lng);
}
