package tfgDaniel.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tfgDaniel.entity.gasolinera.Brand;



public interface BrandRepository extends JpaRepository<Brand, Long> {
	
	Optional<Brand> findByName(String name);
}
