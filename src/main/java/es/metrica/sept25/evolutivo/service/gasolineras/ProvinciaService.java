package es.metrica.sept25.evolutivo.service.gasolineras;

import java.util.List;
import java.util.Optional;

import es.metrica.sept25.evolutivo.entity.gasolinera.Municipio;
import es.metrica.sept25.evolutivo.entity.gasolinera.Provincia;

public interface ProvinciaService {
	void save(Provincia provincia);

	List<Provincia> getProvincias();

	Optional<Provincia> getProvinciaById(Long id);
	
	

	Optional<Provincia> getProvinciaForMunicipio(Municipio mun);
}
