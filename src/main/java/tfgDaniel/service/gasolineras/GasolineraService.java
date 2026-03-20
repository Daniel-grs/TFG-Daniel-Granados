package tfgDaniel.service.gasolineras;

import java.util.List;
import java.util.Optional;

import tfgDaniel.entity.gasolinera.Gasolinera;


public interface GasolineraService {
	List<Gasolinera> getGasolinerasForMunicipio(String municipio);

	Optional<Gasolinera> getGasolineraForId(Long idEstacion);

	List<Gasolinera> getGasolinerasInRadiusCoords(Double latitud, Double longitud, Long radio);

	List<Gasolinera> getGasolinerasInRadiusAddress(String direccion, Long radio);

	List<String> getMarcasFromAllGasolineras();

}