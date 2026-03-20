package tfgDaniel.service.user;

import java.util.List;
import java.util.Optional;

import tfgDaniel.domain.dto.gasolineras.UserSavedGasStationDto;
import tfgDaniel.domain.dto.user.UserBasicInfoDTO;
import tfgDaniel.domain.dto.user.UserDTO;
import tfgDaniel.domain.dto.user.UserResponseDTO;
import tfgDaniel.entity.maps.routes.RoutePreferences;
import tfgDaniel.entity.user.User;
import tfgDaniel.entity.user.UserPreferences.Language;
import tfgDaniel.entity.user.UserPreferences.Theme;
import tfgDaniel.enums.EmissionType;
import tfgDaniel.enums.FuelType;
import tfgDaniel.enums.MapViewType;



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
