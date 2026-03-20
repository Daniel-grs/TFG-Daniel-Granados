package tfgDaniel.service.gasolineras;

import java.util.List;
import java.util.Optional;

import tfgDaniel.entity.gasolinera.Municipio;



public interface MunicipioService {
	List<Municipio> getMunicipios();

	Optional<Municipio> getMunicipioFromString(String munStr);

	Optional<Municipio> getMunicipioFromId(Long idMunicipio);
}
