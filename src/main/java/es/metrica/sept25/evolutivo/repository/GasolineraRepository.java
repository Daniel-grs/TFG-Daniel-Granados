package es.metrica.sept25.evolutivo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.metrica.sept25.evolutivo.entity.gasolinera.Gasolinera;

@Repository
public interface GasolineraRepository extends JpaRepository<Gasolinera, Long> {
	
	Optional<Gasolinera> findByIdEstacion(Long idEstacion);
}