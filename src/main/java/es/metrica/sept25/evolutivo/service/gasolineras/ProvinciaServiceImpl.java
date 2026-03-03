package es.metrica.sept25.evolutivo.service.gasolineras;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import es.metrica.sept25.evolutivo.entity.gasolinera.Municipio;
import es.metrica.sept25.evolutivo.entity.gasolinera.Provincia;
import es.metrica.sept25.evolutivo.repository.ProvinciaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ProvinciaServiceImpl implements ProvinciaService {

	private static final Logger log = LoggerFactory.getLogger(ProvinciaServiceImpl.class);
	
	private static final String API_URL = "https://api.precioil.es/provincias";

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	ProvinciaRepository provinciaRepository;

	public void save(Provincia provincia) {
//		log.info("[prov-service] [" + LocalDateTime.now().toString() + "] "
//				+ "Attempting to save the following province: " + provincia.toString() + ".");
		provinciaRepository.save(provincia);
	}

	@Override
	@Cacheable(value = "provincias", cacheManager = "staticCacheManager")
	public List<Provincia> getProvincias() {
		log.info("[prov-service] [" + LocalDateTime.now().toString() + "] "
				+ "Attempting to retrieve all provinces from the repository.");
		List<Provincia> provList = provinciaRepository.findAll();

		// Si no tenemos, populamos con las españolas
		if (Objects.isNull(provList) || provList.isEmpty()) {
			log.info("[prov-service] [" + LocalDateTime.now().toString() + "] "
					+ "Fetching all provinces from the external API.");
			Provincia[] provArr = restTemplate.getForObject(API_URL, Provincia[].class);
			if (!(Objects.isNull(provArr))) {
//				provList = Arrays.asList(provArr);
				provList = Arrays.stream(provArr).collect(Collectors.toList());
				provinciaRepository.saveAllAndFlush(
						provList.stream().filter(p -> p.getIdProvincia() < 100).collect(Collectors.toList()));
				log.info("[prov-service] [" + LocalDateTime.now().toString() + "] "
						+ "Fetched and stored all provinces from the external API successfully.");
			}
		}

		// Devolvemos las españolas
		return provList;
		
	}

	@Override
	@Cacheable(value = "provinciaById", cacheManager = "staticCacheManager")
	public Optional<Provincia> getProvinciaById(Long id) {
		getProvincias();
		log.info("[prov-service] [" + LocalDateTime.now().toString() + "] "
				+ "Attempting to fetch province with ID: " + id);
		Optional<Provincia> retrieved = provinciaRepository.findById(id);

		if (retrieved.isEmpty()) {
			log.warn("[prov-service] [" + LocalDateTime.now().toString() + "] "
					+ "No province was found with ID: " + id);
		} else {
//			log.info("[prov-service] [" + LocalDateTime.now().toString() + "] "
//					+ "Successfully found the province with ID: " + id);
		}

		return retrieved;
	}

	@Override
	@Cacheable(value = "provinciaForMuni", cacheManager = "staticCacheManager")
	public Optional<Provincia> getProvinciaForMunicipio(Municipio mun) {
		Long provId = mun.getIdProvincia();
		List<Provincia> provList = getProvincias();
		Optional<Provincia> foundProvForMun = provList.stream().filter(p -> p.getIdProvincia() == provId).findFirst();
		
		if (foundProvForMun.isEmpty()) {
			log.warn("[prov-service] [" + LocalDateTime.now().toString() + "] "
					+ "Failed to find the province for municipality: " + mun.toString() + ".");
		} else {
//			log.info("[prov-service] [" + LocalDateTime.now().toString() + "] "
//					+ "Successfully found the province for municipality: " + mun.toString() + ".");
		}
		
		return foundProvForMun;
	}
}
