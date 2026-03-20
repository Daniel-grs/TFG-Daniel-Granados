package tfgDaniel.service.ine;

import java.util.Optional;

public interface INEService {
	
	Optional<String> getCodigoINE(double lat, double lng);
}
