package es.metrica.sept25.evolutivo.service.gasolineras;

import java.util.List;
import java.util.Optional;

import es.metrica.sept25.evolutivo.entity.gasolinera.Municipio;

public interface MunicipioService {
	List<Municipio> getMunicipios();

	Optional<Municipio> getMunicipioFromString(String munStr);

	Optional<Municipio> getMunicipioFromId(Long idMunicipio);
}
