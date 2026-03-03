package es.metrica.sept25.evolutivo.maps.route;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.metrica.sept25.evolutivo.domain.dto.maps.routes.savedRoutes.PointDTO;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.savedRoutes.SavedRouteDTO;
import es.metrica.sept25.evolutivo.entity.maps.routes.Point;
import es.metrica.sept25.evolutivo.entity.maps.routes.SavedRoute;
import es.metrica.sept25.evolutivo.entity.user.User;
import es.metrica.sept25.evolutivo.enums.EmissionType;
import es.metrica.sept25.evolutivo.repository.SavedRouteRepository;
import es.metrica.sept25.evolutivo.repository.UserRepository;
import es.metrica.sept25.evolutivo.service.maps.routes.savedRoutes.SavedRouteServiceImpl;

@ExtendWith(MockitoExtension.class)
public class SavedRouteServiceImplTest {

	@Mock
    private SavedRouteRepository repository;
	
	@Mock
	private UserRepository userRepository;

    @InjectMocks
    private SavedRouteServiceImpl service;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
    }

    @Test
    void saveRoute_ok() {
        List<PointDTO> puntosDTO = List.of(
            new PointDTO("ORIGIN", "Calle Falsa 123"),
            new PointDTO("DESTINATION", "Avenida Siempre Viva")
        );

        SavedRoute savedRoute = new SavedRoute();
        savedRoute.setRouteId(42L);
        savedRoute.setName("Mi Ruta");
        savedRoute.setUser(testUser);

        when(repository.save(any(SavedRoute.class))).thenReturn(savedRoute);

        SavedRouteDTO result = service.saveRoute(
            "Mi Ruta", puntosDTO, testUser, true, false, "es", false, EmissionType.C
        );

        assertNotNull(result);
        assertEquals(42L, result.getRouteId());
        assertEquals("Mi Ruta", result.getName());
        assertEquals(puntosDTO.size(), result.getPoints().size());

        verify(repository).save(any(SavedRoute.class));
    }


    @Test
    void deleteRoute_existingRouteAndSameUser() {
        SavedRoute route = new SavedRoute();
        route.setRouteId(1L);
        route.setUser(testUser);

        when(repository.findByRouteId(1L)).thenReturn(Optional.of(route));

        service.deleteRoute(1L, testUser);

        verify(repository).delete(route);
    }

    @Test
    void deleteRoute_routeNotFound() {
        when(repository.findByRouteId(1L)).thenReturn(Optional.empty());

        service.deleteRoute(1L, testUser);

        verify(repository, never()).delete(any());
    }

    @Test
    void deleteRoute_routeBelongsToOtherUser() {
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setEmail("other@example.com");

        SavedRoute route = new SavedRoute();
        route.setRouteId(1L);
        route.setUser(otherUser);

        when(repository.findByRouteId(1L)).thenReturn(Optional.of(route));

        service.deleteRoute(1L, testUser);

        verify(repository, never()).delete(any());
    }


    @Test
    void getSavedRoute_existingRoute() {
    	
    	System.out.println(service);
    	System.out.println(repository);
    	
        SavedRoute route = new SavedRoute();
        route.setRouteId(1L);
        route.setName("Ruta Test");
        Point p = new Point();
        p.setAddress("Calle 1");
        p.setType(Point.TypePoint.ORIGIN);
        route.setPuntos(List.of(p));

        when(repository.findByRouteId(1L)).thenReturn(Optional.of(route));

        Optional<SavedRouteDTO> result = service.getSavedRoute(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getRouteId());
        assertEquals("Ruta Test", result.get().getName());
        assertEquals(1, result.get().getPoints().size());
        assertEquals("ORIGIN", result.get().getPoints().get(0).getType());
    }

    @Test
    void getSavedRoute_routeNotFound() {
        lenient().when(repository.findById(1L)).thenReturn(Optional.empty());

        Optional<SavedRouteDTO> result = service.getSavedRoute(1L);

        assertTrue(result.isEmpty());
    }
    
    @Test
    void getAllSavedRoutes_userExists_returnsRoutes() {
        SavedRoute route1 = new SavedRoute();
        route1.setRouteId(1L);
        route1.setName("Ruta 1");
        route1.setPuntos(new ArrayList<>());

        SavedRoute route2 = new SavedRoute();
        route2.setRouteId(2L);
        route2.setName("Ruta 2");
        route2.setPuntos(new ArrayList<>());

        testUser.setSavedRoutes(List.of(route1, route2));

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        Optional<List<SavedRouteDTO>> resultOpt = service.getAllSavedRoutes("test@example.com");

        assertTrue(resultOpt.isPresent());
        assertEquals(2, resultOpt.get().size());
        assertEquals("Ruta 1", resultOpt.get().get(0).getName());
        assertEquals("Ruta 2", resultOpt.get().get(1).getName());
    }

    @Test
    void getAllSavedRoutes_userNotFound_returnsEmpty() {
        when(userRepository.findByEmail("noexiste@example.com")).thenReturn(Optional.empty());

        Optional<List<SavedRouteDTO>> resultOpt = service.getAllSavedRoutes("noexiste@example.com");

        assertTrue(resultOpt.isEmpty());
    }
    
    @Test
    void renameRoute_existingRoute_returnsRenamedDTO() {

        SavedRoute route = new SavedRoute();
        route.setRouteId(1L);
        route.setName("Nombre Antiguo");

        Point p = new Point();
        p.setType(Point.TypePoint.ORIGIN);
        p.setAddress("Calle Test");
        route.setPuntos(List.of(p));

        SavedRouteDTO inputDto = new SavedRouteDTO();
        inputDto.setRouteId(1L);

        when(repository.findByRouteId(1L))
                .thenReturn(Optional.of(route));

        when(repository.save(any(SavedRoute.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        SavedRouteDTO result = service.renameRoute("Nombre Nuevo", inputDto);

        assertNotNull(result);
        assertEquals(1L, result.getRouteId());
        assertEquals("Nombre Nuevo", result.getName());
        assertEquals(1, result.getPoints().size());
        assertEquals("ORIGIN", result.getPoints().get(0).getType());

        verify(repository).findByRouteId(1L);
        verify(repository).save(route);
    }

}
