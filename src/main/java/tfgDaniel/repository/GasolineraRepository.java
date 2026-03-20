package tfgDaniel.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tfgDaniel.entity.gasolinera.Gasolinera;


@Repository
public interface GasolineraRepository extends JpaRepository<Gasolinera, Long> {
	
	Optional<Gasolinera> findByIdEstacion(Long idEstacion);
}