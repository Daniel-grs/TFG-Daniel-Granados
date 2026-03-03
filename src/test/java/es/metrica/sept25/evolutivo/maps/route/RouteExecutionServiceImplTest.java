package es.metrica.sept25.evolutivo.maps.route;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.metrica.sept25.evolutivo.domain.dto.maps.routes.Distance;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.Duration;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.Leg;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.Polyline;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.Route;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.RouteGroup;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.Step;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.executionRoutes.RouteExecutionDTO;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.savedRoutes.PointDTO;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.savedRoutes.SavedRouteDTO;
import es.metrica.sept25.evolutivo.enums.EmissionType;
import es.metrica.sept25.evolutivo.service.maps.routes.RoutesService;
import es.metrica.sept25.evolutivo.service.maps.routes.executeRoutes.RouteExecutionServiceImpl;
import es.metrica.sept25.evolutivo.service.maps.routes.savedRoutes.SavedRouteService;

@ExtendWith(MockitoExtension.class)
class RouteExecutionServiceImplTest {

	@Mock
	private SavedRouteService savedRouteService;

	@Mock
	private RoutesService routesService;

	@InjectMocks
	private RouteExecutionServiceImpl service;

	private SavedRouteDTO savedRoute;

	@BeforeEach
	void setUp() {
		// Ruta con 3 puntos
		savedRoute = new SavedRouteDTO();
		savedRoute.setRouteId(1L);
		savedRoute.setName("Test Route");
		savedRoute.setPoints(List.of(new PointDTO("Inicio", "START"), new PointDTO("Intermedio", "WAYPOINT"),
				new PointDTO("Fin", "END")));
	}

	/*
	 * ========================= executeSavedRoute() =========================
	 */

	@Test
	void executeSavedRoute_success() {
		// Mock del SavedRouteService
		when(savedRouteService.getSavedRoute(1L)).thenReturn(Optional.of(savedRoute));

		RouteGroup group = mock(RouteGroup.class);
		Leg leg = mock(Leg.class);

		Distance distance = mock(Distance.class);
		when(distance.getValue()).thenReturn((long) 1000L);

		Duration duration = mock(Duration.class);
		when(duration.getValue()).thenReturn(600L);

		Step step = mock(Step.class);
		Polyline polyline = mock(Polyline.class);
		when(polyline.getPoints()).thenReturn("encoded_polyline");
		when(step.getPolyline()).thenReturn(polyline);

		when(leg.getDistance()).thenReturn(distance);
		when(leg.getDuration()).thenReturn(duration);
		when(leg.getSteps()).thenReturn(List.of(step));

		when(group.getRoutes()).thenReturn(List.of(mock(Route.class)));
		when(group.getRoutes().get(0).getLegs()).thenReturn(List.of(leg));

		// Mock del RoutesService
		when(routesService.getDirections(
				"START",
				"END",
				List.of("WAYPOINT"),
				false, 
				false, 
				"es", 
				false, 
				EmissionType.C))
		.thenReturn(Optional.of(group));

		Optional<RouteExecutionDTO> result = service.executeSavedRoute(1L);

		assertTrue(result.isPresent());
		assertEquals(1000L, result.get().getDistanceMeters());
		assertEquals(600L, result.get().getDurationSeconds());
		assertEquals(List.of("encoded_polyline"), result.get().getPolylines());
	}

	@Test
	void executeSavedRoute_noSavedRoute() {
		when(savedRouteService.getSavedRoute(1L)).thenReturn(Optional.empty());

		Optional<RouteExecutionDTO> result = service.executeSavedRoute(1L);

		assertTrue(result.isEmpty());
	}

	@Test
	void executeSavedRoute_noPoints() {
		SavedRouteDTO emptyRoute = new SavedRouteDTO();
		emptyRoute.setRouteId(2L);
		emptyRoute.setName("Empty Route");
		emptyRoute.setPoints(List.of());

		when(savedRouteService.getSavedRoute(2L)).thenReturn(Optional.of(emptyRoute));

		Optional<RouteExecutionDTO> result = service.executeSavedRoute(2L);

		assertTrue(result.isEmpty());
	}

	@Test
	void executeSavedRoute_noDirections() {
		when(savedRouteService.getSavedRoute(1L)).thenReturn(Optional.of(savedRoute));

		// Devuelve Optional.empty() simulando fallo en RoutesService
		when(routesService.getDirections(anyString(), anyString(), anyList(), anyBoolean(), anyBoolean(), anyString(),
				anyBoolean(), any(EmissionType.class))).thenReturn(Optional.empty());

		Optional<RouteExecutionDTO> result = service.executeSavedRoute(1L);

		assertTrue(result.isEmpty());
	}
}
