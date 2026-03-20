package tfgDaniel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tfgDaniel.entity.ine.INEMunicipio;



@Repository
public interface INEMunicipioRepository extends JpaRepository<INEMunicipio, Long> {
}
