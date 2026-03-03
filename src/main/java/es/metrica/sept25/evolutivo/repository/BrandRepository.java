package es.metrica.sept25.evolutivo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.metrica.sept25.evolutivo.entity.gasolinera.Brand;

public interface BrandRepository extends JpaRepository<Brand, Long> {
	
	Optional<Brand> findByName(String name);
}
