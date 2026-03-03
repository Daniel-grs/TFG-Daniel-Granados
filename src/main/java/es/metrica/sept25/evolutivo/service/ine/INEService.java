package es.metrica.sept25.evolutivo.service.ine;

import java.util.Optional;

public interface INEService {
	
	Optional<String> getCodigoINE(double lat, double lng);
}
