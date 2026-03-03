package es.metrica.sept25.evolutivo.service.gasolineras;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
import es.metrica.sept25.evolutivo.repository.MunicipioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class MunicipioServiceImpl implements MunicipioService {
	
	private static final Logger log = LoggerFactory.getLogger(MunicipioServiceImpl.class);


	private static final String API_URL = "https://api.precioil.es/municipios/provincia/";

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ProvinciaService provinciaService;

	@Autowired
	MunicipioRepository municipioRepository;

	/**
	 * NO USAR SALVO ESTRICTAMENTE NECESARIO
	 */
	@Override
	@Cacheable(value = "municipios", cacheManager = "staticCacheManager")
	public List<Municipio> getMunicipios() {
//		log.info("[mun-service] [" + LocalDateTime.now().toString() + "] "
//				+ "Attempting to retrieve all municipalities.");
		List<Provincia> provList = provinciaService.getProvincias();
		List<Municipio> munList = municipioRepository.findAll();

		List<Long> provIds = provList.stream().map(p -> p.getIdProvincia()).collect(Collectors.toList());

		if (Objects.isNull(munList) || munList.isEmpty()) {
//			log.info("[mun-service] [" + LocalDateTime.now().toString() + "] "
//					+ "Fetching all municipalities from the external API. "
//					+ "This might take a while.");
			ArrayList<Municipio> tempList = new ArrayList<>();
			provIds.forEach(l -> {
				Municipio[] munArr = restTemplate.getForObject(API_URL + l, Municipio[].class);
				if (!Objects.isNull(munArr)) {
					tempList.addAll(Arrays.asList(munArr));
					municipioRepository.saveAllAndFlush(tempList);
				}
			});
//			log.info("[mun-service] [" + LocalDateTime.now().toString() + "] "
//					+ "All municipalities from the external API were retrieved and stored.");
			return tempList;
		}
//		log.info("[mun-service] [" + LocalDateTime.now().toString() + "] "
//				+ "All municipalities were retrieved.");
		return munList;
	}

	@Override
	@Cacheable(value = "municipio_id", cacheManager = "staticCacheManager")
	public Optional<Municipio> getMunicipioFromId(Long idMunicipio) {
		log.info("[mun-service] [" + LocalDateTime.now().toString() + "] "
				+ "Attempting to retrieve municipality with ID: " + idMunicipio + ".");
		List<Municipio> munList = getMunicipios();
		Optional<Municipio> munFromId = munList.stream()
				.filter(m -> m.getIdMunicipio() == idMunicipio)
				.findFirst();
		
		if (munFromId.isEmpty()) {
//			log.warn("[mun-service] [" + LocalDateTime.now().toString() + "] "
//					+ "No municipality was found for the ID: " + idMunicipio + ".");
		} else {
//			log.info("[mun-service] [" + LocalDateTime.now().toString() + "] "
//					+ "Succesfully found a municipality with ID: " + idMunicipio + ".");
		}

		return munFromId;
	}

	@Override
	@Cacheable(value = "municipio_str", cacheManager = "staticCacheManager")
	public Optional<Municipio> getMunicipioFromString(String munStr) {
//		log.info("[mun-service] [" + LocalDateTime.now().toString() + "] "
//				+ "Attempting to retrieve municipality from string identifier: " + munStr + ".");
		List<Municipio> munList = getMunicipios();
		Optional<Municipio> municipalityFromStr = munList.stream()
				.filter(m -> m.getNombreMunicipio()
						.toLowerCase()
						.equals(munStr.toLowerCase()))
				.findFirst();
		
		if (municipalityFromStr.isEmpty()) {
//			log.warn("[mun-service] [" + LocalDateTime.now().toString() + "] "
//					+ "No municipality was found for the string identifier: " + munStr + ".");
		} else {
//			log.info("[mun-service] [" + LocalDateTime.now().toString() + "] "
//					+ "Succesfully found a municipality with string identifier: " + munStr + ".");
		}

		return municipalityFromStr;
	}
}
