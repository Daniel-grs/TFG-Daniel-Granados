package es.metrica.sept25.evolutivo.service.user;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.metrica.sept25.evolutivo.domain.dto.gasolineras.UserSavedGasStationDto;
import es.metrica.sept25.evolutivo.domain.dto.user.UserBasicInfoDTO;
import es.metrica.sept25.evolutivo.domain.dto.user.UserDTO;
import es.metrica.sept25.evolutivo.domain.dto.user.UserResponseDTO;
import es.metrica.sept25.evolutivo.entity.gasolinera.Gasolinera;
import es.metrica.sept25.evolutivo.entity.gasolinera.UserSavedGasStation;
import es.metrica.sept25.evolutivo.entity.maps.routes.RoutePreferences;
import es.metrica.sept25.evolutivo.entity.user.User;
import es.metrica.sept25.evolutivo.entity.user.UserPreferences;
import es.metrica.sept25.evolutivo.entity.user.UserPreferences.Language;
import es.metrica.sept25.evolutivo.entity.user.UserPreferences.Theme;
import es.metrica.sept25.evolutivo.enums.EmissionType;
import es.metrica.sept25.evolutivo.enums.FuelType;
import es.metrica.sept25.evolutivo.enums.MapViewType;
import es.metrica.sept25.evolutivo.repository.GasolineraRepository;
import es.metrica.sept25.evolutivo.repository.UserRepository;
import es.metrica.sept25.evolutivo.service.gasolineras.GasolineraService;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private GasolineraRepository gasolineraRepository;
	
	@Autowired
	private GasolineraService gasolineraService;

	@Override
	public User save(User user) {
		
		log.info("[user-service] [" + LocalDateTime.now().toString() + "] "
                + "Attempting to save user with email: " + user.getEmail());

		if (user.getPassword() != null && !user.getPassword().startsWith("\"^\\\\$2[aby]\\\\$.*\"")) {
			log.info("[user-service] [" + LocalDateTime.now().toString() + "] "
                    + "Encoding password for user: " + user.getEmail());
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		}
		return userRepository.save(user);
	}
	
	@Override
	public Optional<UserBasicInfoDTO> getSimpleInfo(String email) {
		log.info("[user-service] [" + LocalDateTime.now().toString() + "] "
                + "Attempting to retrieve user by email: " + email);
		
		Optional<User> user = userRepository.findByEmail(email);
		
		if (user.isEmpty()) {
	        log.warn("[user-service] [" + LocalDateTime.now().toString() + "] "
	                + "User not found with email: " + email);
			return Optional.empty();
		}
		
		UserBasicInfoDTO simpleUser = new UserBasicInfoDTO(
				email, 
				user.get().getName(), 
				user.get().getSurname()
		);
		log.info("[user-service] [" + LocalDateTime.now().toString() + "] "
				+ "User found with email: " + email);
		return Optional.of(simpleUser);
	}

	@Override
	public Optional<UserResponseDTO> getByEmail(String email) {
		log.info("[user-service] [" + LocalDateTime.now().toString() + "] "
                + "Attempting to retrieve user by email: " + email);
		Optional<User> user = userRepository.findByEmail(email);

	    if (user.isPresent()) {
	        log.info("[user-service] [" + LocalDateTime.now().toString() + "] "
	                + "User found with email: " + email);

	        return Optional.of(mapToResponseDTO(user.get()));
	    } else {
	        log.warn("[user-service] [" + LocalDateTime.now().toString() + "] "
	                + "User not found with email: " + email);

	        return Optional.empty();
	    }
	}

	@Override
	public Optional<User> getEntityByEmail(String email) {
	    log.info("[user-service] Attempting to retrieve USER ENTITY by email: {}", email);
	    return userRepository.findByEmail(email);
	}

	@Override
	public List<UserResponseDTO> getAll() {
		 log.info("[user-service] [" + LocalDateTime.now().toString() + "] "
	                + "Attempting to retrieve all users.");
		 return userRepository.findAll().stream()
		            .map(this::mapToResponseDTO)
		            .toList();
	}

	@Override
	@Transactional
	public Optional<User> createUser(UserDTO userDTO) {
		log.info("[user-service] [" + LocalDateTime.now().toString() + "] "
                + "Attempting to create user with email: " + userDTO.getEmail());
		
		if (!isValidEmail(userDTO.getEmail())) {
	        log.warn("[user-service] [" + LocalDateTime.now() + "] Invalid email format: " + userDTO.getEmail());
	        return Optional.empty();
	    }

	    if (!isValidPassword(userDTO.getPassword())) {
	        log.warn("[user-service] [" + LocalDateTime.now() + "] Invalid password for user: " + userDTO.getEmail());
	        return Optional.empty();
	    }
	    
		if (!userDTO.getPassword().equals(userDTO.getPasswordConfirmation())) {
		    log.warn("[user-service] [" + LocalDateTime.now() + "] Passwords do not match for user: " + userDTO.getEmail());
		    return Optional.empty();
		}
		
		if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
			log.warn("[user-service] [" + LocalDateTime.now().toString() + "] "
                    + "User already exists with email: " + userDTO.getEmail());
	        return Optional.empty();
	    }
		
	    User user = new User();
		user.setName(userDTO.getName());
		user.setSurname(userDTO.getSurname());
		user.setPassword(userDTO.getPassword());
		user.setEmail(userDTO.getEmail());
	    
	    RoutePreferences prefs = new RoutePreferences();
	    
	    prefs.setPreferredBrands(List.of());
        prefs.setRadioKm(1);
        prefs.setFuelType(FuelType.ALL);
        prefs.setMaxPrice(3.0);
        prefs.setMapView(MapViewType.MAP);
        prefs.setAvoidTolls(false);
        prefs.setEmissionType(EmissionType.NONE);
        
        user.setRoutePreferences(prefs);
        
        UserPreferences defaultPrefs = new UserPreferences();
        defaultPrefs.setTheme(Theme.LIGHT);
        defaultPrefs.setLanguage(Language.ES);

        user.setUserPreferences(defaultPrefs);

        log.info("[user-service] [" + LocalDateTime.now().toString() + "] "
                + "User successfully created with email: " + userDTO.getEmail());
        
	    return Optional.of(save(user));
	}
	
	@Override
	@Transactional
	public void updateRoutePreferences(
	        User user,
	        List<String> preferredBrands,
	        int radioKm,
	        FuelType fuelType,
	        double maxPrice,
	        MapViewType mapView,
	        boolean avoidTolls,
	        EmissionType vehicleEmissionType
	) {

		log.info("[user-service] [" + LocalDateTime.now().toString() + "] "
                + "Attempting to update route preferences for user: " + user.getEmail());
		
	    RoutePreferences prefs = user.getRoutePreferences();
	    if (prefs == null) {
	        prefs = new RoutePreferences();
	        user.setRoutePreferences(prefs);
	    }
	    prefs.setPreferredBrands(preferredBrands);
	    prefs.setRadioKm(radioKm);
	    prefs.setFuelType(fuelType);
	    prefs.setMaxPrice(maxPrice);
	    prefs.setMapView(mapView);
	    prefs.setAvoidTolls(avoidTolls);
	    prefs.setEmissionType(vehicleEmissionType);

        userRepository.save(user);
    }
	
	@Override
	@Transactional
	public void updateUserPreferences(
	        User user,
	        Theme theme,
	        Language language
	) {
		log.info("[user-service] [" + LocalDateTime.now().toString() + "] "
                + "Attempting to update user preferences for user: " + user.getEmail());
		
	    UserPreferences prefs = user.getUserPreferences();
	    if (prefs == null) {
	        prefs = new UserPreferences();
	        user.setUserPreferences(prefs);
	    }
	    prefs.setTheme(theme);
	    prefs.setLanguage(language);
	    
	    userRepository.save(user);
	}
	
	@Override
	public Optional<RoutePreferences> getDefaultPreferences() {
		 RoutePreferences prefs = new RoutePreferences();
		 prefs.setPreferredBrands(List.of());
	     prefs.setRadioKm(1);
	     prefs.setFuelType(FuelType.ALL);
	     prefs.setMaxPrice(3.0);
	     prefs.setMapView(MapViewType.MAP);
	     prefs.setAvoidTolls(false);
	     prefs.setEmissionType(EmissionType.NONE);

	     return Optional.of(prefs);

		
		
	}
	
	@Override
	@Transactional
	public void removeGasStation(String email, String alias) {
		log.info("[user-service] [" + LocalDateTime.now().toString() + "] "
                + "Attempting to remove gas station with alias '" + alias
                + "' for user: " + email);
		
	    Optional<User> user = userRepository.findByEmail(email);
	    
	    if (user.isPresent()) {
	    	user.get().getSavedGasStations().removeIf(sg -> sg.getAlias().equalsIgnoreCase(alias));
	    	userRepository.save(user.get());
	    } else {
            log.warn("[user-service] [" + LocalDateTime.now().toString() + "] "
                    + "No user found while removing gas station for email: " + email);
        }
	}
	
	@Override
	@Transactional
	public List<UserSavedGasStationDto> getSavedGasStations(String email) {
        log.info("[user-service] [" + LocalDateTime.now().toString() + "] "
                + "Attempting to retrieve saved gas stations for user: " + email);
        
		return userRepository.findByEmail(email)
	            .map(user -> user.getSavedGasStations().stream()
	                    .map(sg -> {
	                        Gasolinera g = sg.getGasolinera();
	                        return new UserSavedGasStationDto(
	                                sg.getAlias(),
	                                g.getIdEstacion(),
	                                g.getNombreEstacion(),
	                                g.getMarca(),
	                                g.getDireccion()
	                        );
	                    })
	                    .toList()
	            )
	            .orElse(List.of());
	}
	
	@Override
	@Transactional
	public Optional<String> saveGasStation(String email, String alias, Long idEstacion) {
		log.info("[user-service] [" + LocalDateTime.now().toString() + "] "
                + "Attempting to save gas station with id " + idEstacion
                + " for user: " + email);
	    Optional<User> user = userRepository.findByEmail(email);
	    if (user.isEmpty()) {
	    	log.warn("[user-service] [" + LocalDateTime.now().toString() + "] "
                    + "No user found with email: " + email);
	        return Optional.of("Usuario no encontrado");
	    }
	    
	    Optional<Gasolinera> gasolinera = gasolineraRepository.findByIdEstacion(idEstacion);
	    
	    if (gasolinera.isEmpty()) {
	    	log.info("[user-service] [" + LocalDateTime.now().toString() + "] "
                    + "Gas station not found locally, attempting external retrieval for id: "
                    + idEstacion);
	        gasolinera = gasolineraService.getGasolineraForId(idEstacion);
	        gasolinera.ifPresent(gasolineraRepository::save);
	    }
	    
	    if (gasolinera.isEmpty()) {
	    	log.error("[user-service] [" + LocalDateTime.now().toString() + "] "
                    + "Gas station could not be found for id: " + idEstacion);
	        return Optional.of("Gasolinera no encontrada");
	    }
	    
	    boolean exists = user.get().getSavedGasStations().stream()
	        .anyMatch(g -> g.getAlias().equalsIgnoreCase(alias));

	    if (exists) {
	    	log.warn("[user-service] [" + LocalDateTime.now().toString() + "] "
                    + "Alias already exists for user " + email + ": " + alias);
	        return Optional.of("Alias ya existente");
	    }

	    UserSavedGasStation saved = new UserSavedGasStation();
	    saved.setAlias(alias);
	    saved.setUser(user.get());
	    saved.setGasolinera(gasolinera.get());

	    user.get().getSavedGasStations().add(saved);
	    userRepository.save(user.get());
	    
	    log.info("[user-service] [" + LocalDateTime.now().toString() + "] "
                + "Gas station successfully saved for user: " + email);
	    
	    return Optional.empty();
	}
	
	@Override
	@Transactional
	public boolean renameGasStation(String email, String oldAlias, String newAlias) {
		boolean existsNewAlias = getSavedGasStations(email)
				.stream()
				.filter(gStation -> gStation.getAlias().equals(newAlias))
				.count() > 0;
			
		if (existsNewAlias) {
			return false;
		}

		Optional<UserSavedGasStationDto> objective = getSavedGasStations(email)
				.stream()
				.filter(gStation -> gStation.getAlias().equals(oldAlias))
				.findFirst();
		
	    Optional<User> user = userRepository.findByEmail(email);
	    
	    if (user.isEmpty() || objective.isEmpty()) {
            log.warn("[user-service] [" + LocalDateTime.now().toString() + "] "
                    + "No user or gas station found while removing gas station "
                    + "for email: " + email);
            return false;
        }

	    Optional<UserSavedGasStation> oldGS = user.get().getSavedGasStations()
	    		.stream()
	    		.filter(gs -> gs.getAlias().equals(oldAlias))
	    		.findFirst();

	    oldGS.get().setAlias(newAlias);
	    userRepository.save(user.get());

		return true;
	}
	
	private UserResponseDTO mapToResponseDTO(User user) {
	    return new UserResponseDTO(
	        user.getEmail(),
	        user.getName(),
	        user.getSurname(),
	        user.getSavedRoutes(),
	        user.getSavedGasStations()
	    );
	}
	
	private boolean isValidEmail(String email) {
	    String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
	    return email != null && email.matches(emailRegex);
	}

	private boolean isValidPassword(String password) {
	    if (password == null || password.length() < 8) return false;
	    boolean hasLetter = password.chars().anyMatch(Character::isLetter);
	    boolean hasDigit = password.chars().anyMatch(Character::isDigit);
	    return hasLetter && hasDigit;
	}
}

