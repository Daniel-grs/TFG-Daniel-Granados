package es.metrica.sept25.evolutivo.service.maps.geocode;

import java.util.Optional;

public interface ReverseGeocodeService {
	Optional<String> getAddress(double lat, double lng);
}
