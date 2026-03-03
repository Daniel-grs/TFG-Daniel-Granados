package es.metrica.sept25.evolutivo.controller.user;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.metrica.sept25.evolutivo.domain.dto.gasolineras.UserSavedGasStationDto;
import es.metrica.sept25.evolutivo.domain.dto.maps.routes.PreferredBrandsDTO;
import es.metrica.sept25.evolutivo.domain.dto.user.UserBasicInfoDTO;
import es.metrica.sept25.evolutivo.domain.dto.user.UserDTO;
import es.metrica.sept25.evolutivo.domain.dto.user.UserResponseDTO;
import es.metrica.sept25.evolutivo.entity.maps.routes.RoutePreferences;
import es.metrica.sept25.evolutivo.entity.user.User;
import es.metrica.sept25.evolutivo.entity.user.UserPreferences;
import es.metrica.sept25.evolutivo.entity.user.UserPreferences.Language;
import es.metrica.sept25.evolutivo.entity.user.UserPreferences.Theme;
import es.metrica.sept25.evolutivo.enums.EmissionType;
import es.metrica.sept25.evolutivo.enums.FuelType;
import es.metrica.sept25.evolutivo.enums.MapViewType;
import es.metrica.sept25.evolutivo.service.session.CookieService;
import es.metrica.sept25.evolutivo.service.user.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@Tag(name = "User", description = "Endpoints CRUD para la gestión de los usuarios")
@RequestMapping("/api/users")
public class UserController {

	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private CookieService cookieService;

	@Autowired
	private UserService service;

	@Hidden
	@Operation(summary = "Crear un nuevo usuario")
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200", 
					description = "Usuario creado correctamente"
					),
			@ApiResponse(
					responseCode = "400",
					description = "Datos inválidos"
					)
	})
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody(required = true) UserDTO userDTO) {
		log.debug(userDTO.toString());
		Optional<User> user = service.createUser(userDTO);
		if (user.isEmpty()) {
			return ResponseEntity.badRequest().build();
		}

		log.debug(user.toString());
		return ResponseEntity.ok(user.get());
	}

	@Operation(summary = "Obtener datos base de un usuario por mail")
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "Usuario encontrado"
					),
			@ApiResponse(
					responseCode = "404",
					description = "Usuario no encontrado"
					)
	})
	@GetMapping("/get")
	public ResponseEntity<UserBasicInfoDTO> getUser(HttpServletRequest request) {
		String email = cookieService.getCookieValue(request, "sesionActiva").get();
		return service.getSimpleInfo(email)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@Operation(
			summary = "Listar todos los usuarios",
			description = "Devuelve la lista de usuarios registrados. Si no "
					+ "hay usuarios devuelve un 204: No Content."
			)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "Lista de usuarios devuelta correctamente"
					),
			@ApiResponse(
					responseCode = "404",
					description = "No se encontraron usuarios registrados"
					)
	})
	@Hidden
	@GetMapping("/all")
	public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
		List<UserResponseDTO> usuarios = service.getAll();

		if (usuarios.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(usuarios);
	}

	@Operation(
			summary = "Actualizar las preferencias de la ruta.",
			description = "Operación PUT con las preferencias de ruta. Si no se "
					+ "encontró al usuario devuelve un 404: Not Found."
			)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "Preferencias actualizadas correctamente"
					),
			@ApiResponse(
					responseCode = "404",
					description = "Usuario no encontrado"
					)
	})
	@PutMapping("/preferences/update")
	public ResponseEntity<Void> updatePreferences(
			HttpServletRequest request,
			@RequestBody PreferredBrandsDTO brandsDto,
			@RequestParam int radioKm,
			@RequestParam FuelType fuelType,
			@RequestParam double maxPrice,
			@RequestParam MapViewType mapView,
			@RequestParam boolean avoidTolls,
			@RequestParam EmissionType vehicleEmissionType
			) {
		String email = cookieService.getCookieValue(request, "sesionActiva").get();
		Optional<User> userOpt = service.getEntityByEmail(email);

		if (userOpt.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		service.updateRoutePreferences(
				userOpt.get(),
				brandsDto.preferredBrands,
				radioKm,
				fuelType,
				maxPrice,
				mapView,
				avoidTolls,
				vehicleEmissionType
				);

		return ResponseEntity.ok().build();
	}

	@Operation(
			summary = "Actualizar las preferencias del usuario",
			description = "Operación PUT con las preferencias del usuario. "
					+ "Si no se encontró al usuario devuelve un 404: Not Found."
			)
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "Preferencias actualizadas correctamente"
					),
			@ApiResponse(
					responseCode = "404",
					description = "Usuario no encontrado"
					)
	})
	@PutMapping("/preferences/user/update")
	public ResponseEntity<Void> updateUserPreferences(
			HttpServletRequest request,
			@RequestParam Theme theme,
			@RequestParam Language language
			) {
		String email = cookieService.getCookieValue(request, "sesionActiva").get();
		Optional<User> userOpt = service.getEntityByEmail(email);
		if (userOpt.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		service.updateUserPreferences(
				userOpt.get(),
				theme,
				language
				);

		return ResponseEntity.ok().build();
	}

	@Operation(summary = "Obtener las preferencias por defecto de las rutas")
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "Preferencias devueltas correctamente"
					),
			@ApiResponse(
					responseCode = "404",
					description = "Preferencias no encontradas"
					)
	})
	@GetMapping("/preferences/default")
	public ResponseEntity<RoutePreferences> getDefaultPreferences() {

		Optional<RoutePreferences> rpOpt = service.getDefaultPreferences();

		if (rpOpt.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(rpOpt.get());
	}

	@Operation(summary = "Obtener las preferencias de rutas de un usuario")
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200", 
					description = "Preferencias devueltas correctamente"
					),
			@ApiResponse(
					responseCode = "404", 
					description = "Usuario no encontrado"
					)
	})
	@GetMapping("/preferences/get")
	public ResponseEntity<RoutePreferences> getRoutePreferences(HttpServletRequest request) {
		String email = cookieService.getCookieValue(request, "sesionActiva").get();
		Optional<User> userOpt = service.getEntityByEmail(email);

		if (userOpt.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(userOpt.get().getRoutePreferences());
	}

	@Operation(summary = "Obtener las preferencias de usuario")
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "Preferencias devueltas correctamente"
					),
			@ApiResponse(
					responseCode = "404",
					description = "Usuario no encontrado"
					)
	})
	@GetMapping("/preferences/user/get")
	public ResponseEntity<UserPreferences> getUserPreferences(HttpServletRequest request) {
		String email = cookieService.getCookieValue(request, "sesionActiva").get();
		Optional<User> userOpt = service.getEntityByEmail(email);

		if (userOpt.isEmpty() || userOpt.get().getUserPreferences() == null) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(userOpt.get().getUserPreferences());
	}

	@Operation(
			summary = "Obtener marcas de gasolineras preferidas",
			description = "Devuelve la lista de marcas de gasolineras "
					+ "configuradas como preferidas por el usuario"
			)
	@ApiResponses({
		@ApiResponse(
				responseCode = "200",
				description = "Listado de marcas preferidas"
				),
		@ApiResponse(
				responseCode = "404",
				description = "Usuario no encontrado"
				)
	})
	@GetMapping("/preferredBrands/get")
	public ResponseEntity<List<String>> getPreferredBrands(HttpServletRequest request) {
		String email = cookieService.getCookieValue(request, "sesionActiva").get();
		Optional<User> userOpt = service.getEntityByEmail(email);

		if (userOpt.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		List<String> brands = userOpt.get().getRoutePreferences().getPreferredBrands();

		return ResponseEntity.ok(brands);

	}

	@Operation(
			summary = "Añadir gasolinera a favoritos",
			description = "Añade una gasolinera a la lista de gasolineras favoritas del usuario"
			)
	@ApiResponses({
		@ApiResponse(
				responseCode = "200",
				description = "Gasolinera añadida correctamente a favoritos"
				),
		@ApiResponse(
				responseCode = "404",
				description = "Usuario o gasolinera no encontrada"
				)
	})
	@PutMapping("/favouriteStations")
	public ResponseEntity<Void> saveGasStation(
			HttpServletRequest request,
			@RequestParam String alias,
			@RequestParam Long idEstacion) {

		String email = cookieService.getCookieValue(request, "sesionActiva").get();
		Optional<String> opValue = service.saveGasStation(email, alias, idEstacion);
		if (opValue.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok().build();
	}

	@Operation(
			summary = "Eliminar gasolinera de favoritos",
			description = "Elimina una gasolinera de la lista de gasolineras favoritas del usuario"
			)
	@ApiResponses({
		@ApiResponse(
				responseCode = "204",
				description = "Gasolinera eliminada correctamente de favoritos"
				)
	})
	@DeleteMapping("/favouriteStations")
	public ResponseEntity<Void> removeGasStation(
			HttpServletRequest request,
			@RequestParam String alias
			) {
		String email = cookieService.getCookieValue(request, "sesionActiva").get();
		service.removeGasStation(email, alias);
		return ResponseEntity.noContent().build();
	}

	@Operation(
			summary = "Renombrar gasolinera en favoritos",
			description = "Renombra una gasolinera cambiándole el alias"
			)
	@ApiResponses({
		@ApiResponse(
				responseCode = "200",
				description = "Gasolinera renombrada correctamente"
				),
		@ApiResponse(
				responseCode = "404",
				description = "Usuario o gasolinera no encontrada"
				)
	})
	@PostMapping("/favouriteStations")
	public ResponseEntity<Void> renameGasStation(
			HttpServletRequest request,
			@RequestParam String oldAlias,
			@RequestParam String newAlias
			) {

		String email = cookieService.getCookieValue(request, "sesionActiva").get();
		if (service.renameGasStation(email, oldAlias, newAlias)) {
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.notFound().build();
	}

	@Operation(
			summary = "Obtener gasolineras favoritas del usuario",
			description = "Devuelve el listado de gasolineras marcadas como "
					+ "favoritas por el usuario"
			)
	@ApiResponses({
		@ApiResponse(
				responseCode = "200", description = "Listado de gasolineras favoritas"
				)
	})
	@GetMapping("/favouriteStations")
	public ResponseEntity<List<UserSavedGasStationDto>> getSavedGasStations(
			HttpServletRequest request) {
		String email = cookieService.getCookieValue(request, "sesionActiva").get();
		return ResponseEntity.ok(service.getSavedGasStations(email));
	}

}
