package tfgDaniel.service.maps.routes;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.LatLng;

import tfgDaniel.domain.dto.maps.routes.Coords;
import tfgDaniel.domain.dto.maps.routes.CoordsWithWeather;
import tfgDaniel.domain.dto.maps.routes.Leg;
import tfgDaniel.domain.dto.maps.routes.RouteGroup;
import tfgDaniel.domain.dto.maps.routes.RoutePointWeatherDTO;
import tfgDaniel.domain.dto.maps.routes.Step;
import tfgDaniel.domain.dto.weather.Dia;
import tfgDaniel.domain.dto.weather.EstadoCielo;
import tfgDaniel.domain.dto.weather.Temperatura;
import tfgDaniel.domain.dto.weather.Weather;
import tfgDaniel.enums.EmissionType;
import tfgDaniel.service.gasolineras.GasolineraService;
import tfgDaniel.service.ine.INEService;
import tfgDaniel.service.maps.geocode.GeocodeService;
import tfgDaniel.service.maps.geocode.ReverseGeocodeService;
import tfgDaniel.service.weather.WeatherService;



@Service
public class RoutesServiceImpl implements RoutesService {

	private static final Logger log = LoggerFactory.getLogger(RoutesServiceImpl.class);

	
	private static final String API_URL = "https://maps.googleapis.com/maps/api/directions/json";
	private static final String MODE = "driving";
	private static final String OPTIMIZE = "optimize:true|";
	private static final String AVOID_TOLLS = "tolls";

	@Value("${evolutivo.api_key_google}")
	private String API_KEY_GOOGLE;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private INEService ineService;

	@Autowired
	GasolineraService gasolineraService;

	@Autowired
	private WeatherService weatherService;
	
	@Autowired
	private ReverseGeocodeService reverseGeocodeService;
	
	@Autowired
	private GeocodeService geocodeService;

	@Override
	public Optional<RouteGroup> getDirections(String origin, String destination, List<String> waypoints,
	        boolean optimizeWaypoints, boolean optimizeRoute, String language, boolean avoidTolls) {

	    Set<String> invalidDirections = new HashSet<>();

	    Optional<Coords> originCoords = geocodeService.getCoordinates(normalizeText(origin) + ", España");
	    if (originCoords.isEmpty()) {
	        invalidDirections.add(origin);
	    }

	    Optional<Coords> destinationCoords = geocodeService.getCoordinates(normalizeText(destination) + ", España");
	    if (destinationCoords.isEmpty()) {
	        invalidDirections.add(destination);
	    }

	    List<Coords> waypointsCoords = new ArrayList<>();

	    if (!waypoints.isEmpty()) {
	        for (String waypoint : waypoints) {
	            Optional<Coords> waypointCoords = geocodeService.getCoordinates(normalizeText(waypoint) + ", España");

	            if (waypointCoords.isEmpty()) {
	                invalidDirections.add(waypoint);
	            } else {
	                waypointsCoords.add(waypointCoords.get());
	            }
	        }
	    }

	    if (!invalidDirections.isEmpty()) {
	        log.error("[routes-service] [" + LocalDateTime.now() + "] "
	                + "Invalid directions were found: " + invalidDirections);
	        return Optional.empty();
	    }

	    UriComponentsBuilder url = UriComponentsBuilder
	            .fromUriString(API_URL)
	            .queryParam("mode", MODE)
	            .queryParam("language", language)
	            .queryParam("key", API_KEY_GOOGLE)
	            .queryParam("origin", originCoords.get().toString());

	    if (avoidTolls) {
	        url.queryParam("avoid", AVOID_TOLLS);
	    }

	    if (!optimizeRoute) {
	        url.queryParam("destination", destinationCoords.get().toString());
	    }

	    boolean shouldOptimize = (!waypoints.isEmpty() && optimizeWaypoints) || optimizeRoute;

	    if (optimizeRoute) {
	        waypointsCoords.add(destinationCoords.get());
	        url.queryParam("destination", originCoords.get().toString());
	    }

	    String result = getUrl(waypointsCoords, url, shouldOptimize);

	    System.out.println("URL FINAL GOOGLE = " + result);

	    RouteGroup response = restTemplate.getForObject(result, RouteGroup.class);

	    if (response == null || response.getRoutes() == null || response.getRoutes().isEmpty()) {
	        System.out.println("Google no devolvió rutas");
	        return Optional.empty();
	    }

	    if (!waypoints.isEmpty() && optimizeRoute) {
	        response = deleteLastLeg(response);
	    }

	    log.info("[routes-service] [" + LocalDateTime.now() + "] "
	            + "Successfully calculated directions for the given data: ["
	            + origin + " - " + waypoints + " - " + destination
	            + " | OptWay=" + optimizeWaypoints + ", OptRoute=" + optimizeRoute
	            + ", lang=" + language + "]");

	    return Optional.of(response);
	}

	private RouteGroup deleteLastLeg(RouteGroup response) {
		List<Leg> legs = response.getRoutes().getFirst().getLegs();
		legs.removeLast();
		response.getRoutes().getFirst().setLegs(legs);

		return response;
	}

	@Override
	public String getUrl(List<Coords> waypoints, UriComponentsBuilder url, boolean shouldOptimize) {
	    log.info("[routes-service] [" + LocalDateTime.now() + "] "
	            + "Attempting to compose a URL from a list of waypoints.");

	    if (waypoints == null || waypoints.isEmpty()) {
	        return url.toUriString();
	    }

	    String waypointValue = waypoints.stream()
	            .map(Coords::toString)
	            .collect(Collectors.joining("|"));

	    if (shouldOptimize) {
	        waypointValue = OPTIMIZE + waypointValue;
	    }

	    url.queryParam("waypoints", "");
	    return url.toUriString() + waypointValue;
	}

	@Override
	public List<Coords> extractRoutePoints(RouteGroup routeGroup) {
		if (routeGroup == null || routeGroup.getRoutes() == null) {
			log.error("[routes-service] [" + LocalDateTime.now().toString() + "] "
					+ "No route or routeGroup were given.");
			return List.of();
		}

		return routeGroup.getRoutes().stream()
				.flatMap(route -> route.getLegs().stream())
				.flatMap(leg -> leg.getSteps().stream())
				.map(Step::getStartLocation)
				.collect(Collectors.toList());
	}
	
	@Override
	public List<Coords> extractRoutePolylinePoints(RouteGroup routeGroup) {
		log.info("[routes-service] [" + LocalDateTime.now().toString() + "] "
				+ "Attempting to extract route polylinePoints for a given route.");
		if (routeGroup == null || routeGroup.getRoutes() == null) {
			log.error("[routes-service] [" + LocalDateTime.now().toString() + "] "
					+ "No route or routeGroup were given.");
			return List.of();
		}
		
		return routeGroup.getRoutes().stream()
				.flatMap(route -> route.getLegs().stream())
				.flatMap(leg -> leg.getSteps().stream())
				.map(step -> decodePolyline(step.getPolyline().getPoints()))
				.flatMap(coordsList -> coordsList.stream())
				.collect(Collectors.toList());
	}

	@Override
	public List<Coords> decodePolyline(String polylinePoints) {
		log.info("[routes-service] [" + LocalDateTime.now().toString() + "] "
				+ "Attempting to decode into coordinates a given route polyline.");
		EncodedPolyline polyline = new EncodedPolyline(polylinePoints);
		List<LatLng> latLngs = polyline.decodePath();
		
		return latLngs.stream()
				.map(latLng -> new Coords(latLng.lat, latLng.lng))
				.collect(Collectors.toList());
	}

	@Override
	public List<RoutePointWeatherDTO> getWeatherForRoute(RouteGroup routeGroup) {
		log.info("[routes-service] [" + LocalDateTime.now() + "] "
				+ "Attempting to get weather for each point of the route.");

		List<Coords> allPoints = extractRoutePolylinePoints(routeGroup);
		List<Coords> sampledPoints = getKeyRoutePoints(routeGroup);

		if (sampledPoints.isEmpty() || allPoints.isEmpty()) {
			return List.of();
		}

		long totalSeconds = routeGroup.getRoutes().get(0).getLegs().stream()
				.mapToLong(leg -> leg.getDuration().getValue())
				.sum();

		LocalDateTime departureTime = LocalDateTime.now();

		Map<String, IndexedCoords> coordsPorMunicipio = new LinkedHashMap<>();

		for (Coords coords : sampledPoints) {
			Optional<String> codigoINE = ineService.getCodigoINE(coords.getLat(), coords.getLng());

			if (codigoINE.isEmpty()) {
				log.warn("[routes-service] [" + LocalDateTime.now() + "] "
						+ "No INE code could be extracted for the given coords: "
						+ coords);
				continue;
			}

			int pointIndex = findClosestPointIndex(allPoints, coords);

			coordsPorMunicipio.putIfAbsent(
					codigoINE.get(),
					new IndexedCoords(coords, pointIndex)
			);
		}

		return coordsPorMunicipio.entrySet().stream()
				.map(entry -> {
					String codigoINE = entry.getKey();
					IndexedCoords indexedCoords = entry.getValue();
					Coords coords = indexedCoords.getCoords();
					int pointIndex = indexedCoords.getIndex();

					String address = reverseGeocodeService
							.getAddress(coords.getLat(), coords.getLng())
							.orElse("Ubicación desconocida");

					Optional<Weather> weatherOpt = weatherService.getWeather(codigoINE);

					if (weatherOpt.isEmpty()) {
						return new RoutePointWeatherDTO(
								address,
								null,
								"Sin datos meteorológicos",
								null
						);
					}

					Weather weather = weatherOpt.get();
					Dia dia = weather.getPrediccion().getDia().get(0);

					Map<Integer, String> mapaDescripciones = new HashMap<>();
					if (dia.getEstadoCielo() != null) {
						for (EstadoCielo estado : dia.getEstadoCielo()) {
							mapaDescripciones.put(
									estado.getPeriodo(),
									estado.getDescripcion()
							);
						}
					}

					Map<Integer, Double> mapaTemperaturas = new HashMap<>();
					if (dia.getTemperatura() != null) {
						for (Temperatura temp : dia.getTemperatura()) {
							mapaTemperaturas.put(
									temp.getPeriodo(),
									temp.getValue()
							);
						}
					}

					double ratio = allPoints.size() > 1
							? (double) pointIndex / (allPoints.size() - 1)
							: 0.0;

					long secondsToPoint = (long) (totalSeconds * ratio);
					LocalDateTime estimatedArrival = departureTime.plusSeconds(secondsToPoint);
					int estimatedHour = estimatedArrival.getHour();

					String weatherDescription = mapaDescripciones.getOrDefault(
							estimatedHour,
							"Sin descripción disponible"
					);

					Double temperature = mapaTemperaturas.get(estimatedHour);

					return new RoutePointWeatherDTO(
							address,
							estimatedHour,
							weatherDescription,
							temperature
					);
				})
				.toList();
	}

	private int calculateMaxCalls(long meters) {

        if (meters <= 5_000) return 2;
        if (meters <= 20_000) return 4;
        if (meters <= 100_000) return 8;
        return 14;
    }
	private List<Coords> getSampledRoutePoints(RouteGroup routeGroup) {

	    List<Coords> allPoints = extractRoutePolylinePoints(routeGroup);

	    if (allPoints.isEmpty()) {
	        log.warn("[routes-service] [" + LocalDateTime.now() + "] No polyline points extracted.");
	        return List.of();
	    }

	    long totalMeters = routeGroup.getRoutes().get(0).getLegs().stream()
	            .mapToLong(leg -> leg.getDistance().getValue())
	            .sum();

	    int maxCalls = calculateMaxCalls(totalMeters);

	    int step = Math.max(
	            1,
	            (int) Math.ceil((double) allPoints.size() / maxCalls)
	    );

	    return IntStream.range(0, allPoints.size())
	            .filter(i -> i % step == 0 || i == allPoints.size() - 1)
	            .mapToObj(allPoints::get)
	            .toList();
	}
	private List<Coords> getKeyRoutePoints(RouteGroup routeGroup) {
	    List<Coords> allPoints = extractRoutePolylinePoints(routeGroup);

	    if (allPoints.isEmpty()) {
	        return List.of();
	    }

	    Coords origin = allPoints.getFirst();
	    Coords destination = allPoints.getLast();
	    Coords middle = allPoints.get(allPoints.size() / 2);

	    List<Coords> result = new ArrayList<>();
	    result.add(origin);

	    if (!origin.equals(middle) && !destination.equals(middle)) {
	        result.add(middle);
	    }

	    if (!origin.equals(destination)) {
	        result.add(destination);
	    }

	    return result;
	}
	
	@Override
	public List<Coords> getGasStationsCoordsForRoute(RouteGroup routeGroup, Long radius) {
		log.info("[routes-service] [" + LocalDateTime.now().toString() + "] "
				+ "Attempting to extract coordinates for all gas stations in the route's radius: " 
				+ radius + ".");
		List<Coords> sampledRoutePoints = getSampledRoutePoints(routeGroup);

	    if (sampledRoutePoints.isEmpty()) {
	        return List.of();
	    }
		
	    List<Coords> stationsForRoute = sampledRoutePoints.stream()
	    	    .flatMap(point -> gasolineraService
	    	                        .getGasolinerasInRadiusCoords(point.getLat(), point.getLng(), radius)
	    	                        .stream()
	    	    )
	    	    .map(station -> new Coords(station.getLatitud(), station.getLongitud()))
	    	    .distinct()
	    	    .collect(Collectors.toList());
		
		return stationsForRoute;
	}
	
	@Override
	public List<Coords> getLegCoords(RouteGroup routeGroup) {
		log.info("[routes-service] [" + LocalDateTime.now().toString() + "] "
				+ "Attempting to extract leg coordinates for a given route.");
		List<Coords> legCoords = routeGroup.getRoutes().stream()
				.flatMap(route -> route.getLegs().stream())
				.flatMap(leg -> {
					Coords startLoc = leg.getStartLocation();
					Coords endLoc = leg.getEndLocation();
					List<Coords> legList = List.of(startLoc, endLoc);
					return legList.stream();
				})
				.distinct()
				.collect(Collectors.toList());

		log.info("[routes-service] [" + LocalDateTime.now().toString() + "] "
				+ "Successfully retrieved the leg coordinates for the given route.");
		return legCoords;
	}
	private static class IndexedCoords {
		private final Coords coords;
		private final int index;

		public IndexedCoords(Coords coords, int index) {
			this.coords = coords;
			this.index = index;
		}

		public Coords getCoords() {
			return coords;
		}

		public int getIndex() {
			return index;
		}
	}
	
	private int findClosestPointIndex(List<Coords> allPoints, Coords target) {
		int closestIndex = 0;
		double minDistance = Double.MAX_VALUE;

		for (int i = 0; i < allPoints.size(); i++) {
			Coords current = allPoints.get(i);
			double distance = distanceSquared(current, target);

			if (distance < minDistance) {
				minDistance = distance;
				closestIndex = i;
			}
		}

		return closestIndex;
	}

	private double distanceSquared(Coords a, Coords b) {
		double latDiff = a.getLat() - b.getLat();
		double lngDiff = a.getLng() - b.getLng();
		return latDiff * latDiff + lngDiff * lngDiff;
	}

	private String normalizeText(String text) {
	    if (text == null) return "";
	    return Normalizer.normalize(text, Normalizer.Form.NFD)
	            .replaceAll("\\p{M}", "")
	            .trim();
	}
}
