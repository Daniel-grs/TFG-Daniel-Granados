package es.metrica.sept25.evolutivo.service.user;

import java.util.List;
import java.util.Optional;

import es.metrica.sept25.evolutivo.domain.dto.gasolineras.UserSavedGasStationDto;
import es.metrica.sept25.evolutivo.domain.dto.user.UserBasicInfoDTO;
import es.metrica.sept25.evolutivo.domain.dto.user.UserDTO;
import es.metrica.sept25.evolutivo.domain.dto.user.UserResponseDTO;
import es.metrica.sept25.evolutivo.enums.EmissionType;
import es.metrica.sept25.evolutivo.enums.FuelType;
import es.metrica.sept25.evolutivo.enums.MapViewType;
import es.metrica.sept25.evolutivo.entity.maps.routes.RoutePreferences;
import es.metrica.sept25.evolutivo.entity.user.User;
import es.metrica.sept25.evolutivo.entity.user.UserPreferences.Language;
import es.metrica.sept25.evolutivo.entity.user.UserPreferences.Theme;

public interface UserService {
    User save(User user);
    
    Optional<UserBasicInfoDTO> getSimpleInfo(String email);

    Optional<UserResponseDTO> getByEmail(String mail);

    List<UserResponseDTO> getAll();

	Optional<User> createUser(UserDTO userDTO);

	void removeGasStation(String email, String alias);

	List<UserSavedGasStationDto> getSavedGasStations(String email);

	Optional<String> saveGasStation(String email, String alias, Long idEstacion);

	boolean renameGasStation(String email, String oldAlias, String newAlias);

	Optional<User> getEntityByEmail(String email);

	void updateUserPreferences(User user, Theme theme, Language language);

	void updateRoutePreferences(User user, List<String> preferredBrands, int radioKm, FuelType fuelType,
			double maxPrice, MapViewType mapView, boolean avoidTolls, EmissionType vehicleEmissionType);

	Optional<RoutePreferences> getDefaultPreferences();
}
