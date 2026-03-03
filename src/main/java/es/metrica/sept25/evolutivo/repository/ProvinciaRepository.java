package es.metrica.sept25.evolutivo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.metrica.sept25.evolutivo.entity.gasolinera.Provincia;

@Repository
public interface ProvinciaRepository extends JpaRepository<Provincia, Long> {

	long count();

	List<Provincia> findAll();
}