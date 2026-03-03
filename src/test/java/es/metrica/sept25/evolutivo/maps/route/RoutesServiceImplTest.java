package es.metrica.sept25.evolutivo.maps.route;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.util.UriComponentsBuilder;

import es.metrica.sept25.evolutivo.domain.dto.maps.routes.*;
import es.metrica.sept25.evolutivo.domain.dto.weather.*;
import es.metrica.sept25.evolutivo.entity.gasolinera.Gasolinera;
import es.metrica.sept25.evolutivo.enums.EmissionType;
import es.metrica.sept25.evolutivo.service.gasolineras.GasolineraService;
import es.metrica.sept25.evolutivo.service.ine.INEService;
import es.metrica.sept25.evolutivo.service.maps.geocode.GeocodeService;
import es.metrica.sept25.evolutivo.service.maps.geocode.ReverseGeocodeService;
import es.metrica.sept25.evolutivo.service.maps.routes.RoutesServiceImpl;
import es.metrica.sept25.evolutivo.service.weather.WeatherService;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class RoutesServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private INEService ineService;

    @Mock
    private GasolineraService gasolineraService;

    @Mock
    private WeatherService weatherService;

    @Mock
    private ReverseGeocodeService reverseGeocodeService;

    @Mock
    private GeocodeService geocodeService;

    @InjectMocks
    private RoutesServiceImpl service;

    private Coords coords1;
    private Coords coords2;

    @BeforeEach
    void setUp() {
        coords1 = new Coords(40.0, -3.0);
        coords2 = new Coords(41.0, -4.0);
    }

    /* =========================
       getDirections()
       ========================= */

    @Test
    void getDirections_success() {
        when(geocodeService.getCoordinates("Origin"))
                .thenReturn(Optional.of(coords1));
        when(geocodeService.getCoordinates("Destination"))
                .thenReturn(Optional.of(coords2));

        RouteGroup rg = simpleRouteGroup();
        when(restTemplate.getForObject(anyString(), eq(RouteGroup.class)))
                .thenReturn(rg);

        Optional<RouteGroup> result = service.getDirections(
                "Origin", "Destination",
                List.of(), false, false,
                "es", false, EmissionType.C
        );

        assertTrue(result.isPresent());
    }

    @Test
    void getDirections_invalidOrigin_returnsEmpty() {
        when(geocodeService.getCoordinates("Origin"))
                .thenReturn(Optional.empty());
        when(geocodeService.getCoordinates("Destination"))
                .thenReturn(Optional.of(coords2));

        Optional<RouteGroup> result = service.getDirections(
                "Origin", "Destination",
                List.of(), false, false,
                "es", false, EmissionType.C
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void getDirections_invalidWaypoint_returnsEmpty() {
        when(geocodeService.getCoordinates("Origin"))
                .thenReturn(Optional.of(coords1));
        when(geocodeService.getCoordinates("Destination"))
                .thenReturn(Optional.of(coords2));
        when(geocodeService.getCoordinates("BadWaypoint"))
                .thenReturn(Optional.empty());

        Optional<RouteGroup> result = service.getDirections(
                "Origin", "Destination",
                List.of("BadWaypoint"),
                false, false,
                "es", false, EmissionType.C
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void getDirections_optimizeRoute_deletesLastLeg() {
        when(geocodeService.getCoordinates(anyString()))
                .thenReturn(Optional.of(coords1));

        Leg leg1 = new Leg();
        Leg leg2 = new Leg();

        Route route = new Route();
        route.setLegs(new ArrayList<>(List.of(leg1, leg2)));

        RouteGroup rg = new RouteGroup();
        rg.setRoutes(List.of(route));

        when(restTemplate.getForObject(anyString(), eq(RouteGroup.class)))
                .thenReturn(rg);

        Optional<RouteGroup> result = service.getDirections(
                "Origin", "Destination",
                List.of("Waypoint"),
                true, true,
                "es", false, EmissionType.C
        );

        assertEquals(1,
                result.get().getRoutes().get(0).getLegs().size());
    }

    /* =========================
       getUrl()
       ========================= */

    @Test
    void getUrl_buildsCorrectUrl() {
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromUriString("http://test");

        String url = service.getUrl(
                List.of(new Coords(40, -3), new Coords(41, -4)),
                builder
        );

        assertTrue(url.contains("40"));
        assertTrue(url.contains("41"));
        assertTrue(url.contains("|"));
    }

    /* =========================
       extractRoutePoints / polyline
       ========================= */

    @Test
    void extractRoutePoints_validRouteGroup_returnsStartLocations() {

        // Step con startLocation
        Step step = new Step();
        Coords start = new Coords(40.0, -3.0);
        step.setStartLocation(start);

        // Leg con steps
        Leg leg = new Leg();
        leg.setSteps(List.of(step));

        // Route con legs
        Route route = new Route();
        route.setLegs(List.of(leg));

        // RouteGroup válido
        RouteGroup routeGroup = new RouteGroup();
        routeGroup.setRoutes(List.of(route));

        // Ejecutar
        List<Coords> result = service.extractRoutePoints(routeGroup);

        // Verificaciones
        assertEquals(1, result.size());
        assertEquals(40.0, result.get(0).getLat());
        assertEquals(-3.0, result.get(0).getLng());
    }
    
    @Test
    void extractRoutePoints_nullRouteGroup_returnsEmpty() {
        assertTrue(service.extractRoutePoints(null).isEmpty());
    }

    @Test
    void extractRoutePolylinePoints_null_returnsEmpty() {
        assertTrue(service.extractRoutePolylinePoints(null).isEmpty());
    }

    @Test
    void decodePolyline_ok() {
        String polyline = "}_ilFf|ys@_@hA";
        List<Coords> result = service.decodePolyline(polyline);

        assertEquals(2, result.size());
    }

    /* =========================
       getWeatherForRoute()
       ========================= */

    @Test
    void getWeatherForRoute_success() {

        RouteGroup rg = simpleRouteGroup();

        when(ineService.getCodigoINE(anyDouble(), anyDouble()))
                .thenReturn(Optional.of("28079"));

        when(reverseGeocodeService.getAddress(anyDouble(), anyDouble()))
                .thenReturn(Optional.of("Madrid"));

        Weather weather = new Weather();
        Dia dia = new Dia();
        dia.setEstadoCielo(List.of(new EstadoCielo(0, "Despejado")));
        dia.setTemperatura(List.of(new Temperatura(0, 20.0)));

        Prediccion pred = new Prediccion();
        pred.setDia(List.of(dia));
        weather.setPrediccion(pred);

        when(weatherService.getWeather("28079"))
                .thenReturn(Optional.of(weather));

        List<CoordsWithWeather> result =
                service.getWeatherForRoute(rg);

        assertEquals(1, result.size());
        assertEquals("Madrid", result.get(0).getAddress());
    }

    @Test
    void getWeatherForRoute_noWeather_returnsEmptyMaps() {

        RouteGroup rg = simpleRouteGroup();

        when(ineService.getCodigoINE(anyDouble(), anyDouble()))
                .thenReturn(Optional.of("28079"));
        when(weatherService.getWeather("28079"))
                .thenReturn(Optional.empty());
        when(reverseGeocodeService.getAddress(anyDouble(), anyDouble()))
                .thenReturn(Optional.of("Madrid"));

        List<CoordsWithWeather> result =
                service.getWeatherForRoute(rg);

        assertTrue(result.get(0).getWeatherDescription().isEmpty());
        assertTrue(result.get(0).getTemperatures().isEmpty());
    }

    /* =========================
       getGasStationsCoordsForRoute()
       ========================= */

    @Test
    void getGasStationsCoordsForRoute_success() {

        Gasolinera gas = new Gasolinera();
        gas.setLatitud(40.0);
        gas.setLongitud(-3.0);

        when(gasolineraService.getGasolinerasInRadiusCoords(
                anyDouble(), anyDouble(), eq(10L)))
                .thenReturn(List.of(gas));

        RouteGroup rg = simpleRouteGroup();

        List<Coords> result =
                service.getGasStationsCoordsForRoute(rg, 10L);

        assertEquals(1, result.size());
    }

    /* =========================
       getLegCoords()
       ========================= */

    @Test
    void getLegCoords_success() {

        Leg leg = new Leg();
        leg.setStartLocation(coords1);
        leg.setEndLocation(coords2);

        Route route = new Route();
        route.setLegs(List.of(leg));

        RouteGroup rg = new RouteGroup();
        rg.setRoutes(List.of(route));

        List<Coords> result = service.getLegCoords(rg);

        assertEquals(2, result.size());
    }

    /* =========================
       Helper
       ========================= */

    private RouteGroup simpleRouteGroup() {
        Polyline polyline = new Polyline();
        polyline.setPoints("}_ilFf|ys@_@hA");

        Step step = new Step();
        step.setPolyline(polyline);

        Distance distance = new Distance();
        distance.setValue(10_000L);

        Leg leg = new Leg();
        leg.setSteps(List.of(step));
        leg.setDistance(distance);
        leg.setStartLocation(coords1);
        leg.setEndLocation(coords2);

        Route route = new Route();
        route.setLegs(List.of(leg));

        RouteGroup rg = new RouteGroup();
        rg.setRoutes(List.of(route));

        return rg;
    }
}
