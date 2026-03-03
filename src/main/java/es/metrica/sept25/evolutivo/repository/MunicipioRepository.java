package es.metrica.sept25.evolutivo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.metrica.sept25.evolutivo.entity.gasolinera.Municipio;

@Repository
public interface MunicipioRepository extends JpaRepository<Municipio, Long> {
}
