package es.metrica.sept25.evolutivo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.metrica.sept25.evolutivo.entity.ine.INEMunicipio;

@Repository
public interface INEMunicipioRepository extends JpaRepository<INEMunicipio, Long> {
}
