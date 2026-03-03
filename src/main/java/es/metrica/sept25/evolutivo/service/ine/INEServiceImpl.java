package es.metrica.sept25.evolutivo.service.ine;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import es.metrica.sept25.evolutivo.entity.ine.INEMunicipio;
import es.metrica.sept25.evolutivo.repository.INEMunicipioRepository;
import es.metrica.sept25.evolutivo.service.maps.geocode.GeocodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class INEServiceImpl implements INEService {

	private static final Logger log = LoggerFactory.getLogger(INEServiceImpl.class);

	
	@Autowired
	private GeocodeService geocodeService;

	@Autowired
	private INEMunicipioRepository ineMunicipioRepository;

	@Autowired
	private RestTemplate restTemplate;

	private static final String INE_URL = "http://servicios.ine.es/wstempus/js/ES/VALORES_VARIABLE/19";

	@Override
	@Cacheable("codigoIne")
	public Optional<String> getCodigoINE(double lat, double lng) {
		log.info("[ine-service] [" + LocalDateTime.now().toString() + "] "
				+ "Attempting to retrieve INE code for data:"
				+ "[lat=("+ lat + "), lng=(" + lng + ")].");
		Optional<String> municipio = geocodeService.getMunicipio(lat, lng);

		if (municipio.isEmpty()) {
			log.info("[ine-service] [" + LocalDateTime.now().toString() + "] "
					+ "No municipality was found for data:"
					+ "[lat=("+ lat + "), lng=(" + lng + ")].");
			return Optional.empty();
		}

		if (ineMunicipioRepository.count() == 0) {
			log.info("[ine-service] [" + LocalDateTime.now().toString() + "] "
					+ "Fetching all INE municipalities from external API. This might take a while ...");
			UriComponentsBuilder url = UriComponentsBuilder.fromUriString(INE_URL).queryParam("page", "1");
			INEMunicipio[] response = restTemplate.getForObject(url.toUriString(), INEMunicipio[].class);

			if (response == null) {
				log.warn("[ine-service] [" + LocalDateTime.now().toString() + "] "
						+ "Couldn't retrieve all INE municipalities from external API.");
				return Optional.empty();
			}

			List<INEMunicipio> ineMunList = Arrays.asList(response);
			ineMunicipioRepository.saveAllAndFlush(ineMunList);
			log.warn("[ine-service] [" + LocalDateTime.now().toString() + "] "
					+ "Retrieved and saved all INE municipalities from external API.");
		}

		List<INEMunicipio> listaGuardada = ineMunicipioRepository.findAll();

				
		Optional<INEMunicipio> optMun = listaGuardada.stream().filter(m -> m.getNombre().equalsIgnoreCase(municipio.get()))
				.findFirst();
		
		
		if (optMun.isEmpty()) {
			log.warn("[ine-service] [" + LocalDateTime.now().toString() + "] "
					+ "Couldn't find the INE municipality for data:"
					+ "[lat=("+ lat + "), lng=(" + lng + ")].");
			return Optional.empty();
		} else {
			log.info("[ine-service] [" + LocalDateTime.now().toString() + "] "
					+ "Found the corresponding INE municipality for data:"
					+ "[lat=("+ lat + "), lng=(" + lng + ")].");
			return Optional.of(optMun.get().getCodigoINE());
		}
	}
}
