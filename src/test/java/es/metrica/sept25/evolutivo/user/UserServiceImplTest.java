package es.metrica.sept25.evolutivo.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import es.metrica.sept25.evolutivo.domain.dto.gasolineras.UserSavedGasStationDto;
import es.metrica.sept25.evolutivo.domain.dto.user.UserBasicInfoDTO;
import es.metrica.sept25.evolutivo.domain.dto.user.UserDTO;
import es.metrica.sept25.evolutivo.domain.dto.user.UserResponseDTO;
import es.metrica.sept25.evolutivo.entity.gasolinera.Gasolinera;
import es.metrica.sept25.evolutivo.entity.gasolinera.UserSavedGasStation;
import es.metrica.sept25.evolutivo.entity.maps.routes.RoutePreferences;
import es.metrica.sept25.evolutivo.entity.user.User;
import es.metrica.sept25.evolutivo.entity.user.UserPreferences.Language;
import es.metrica.sept25.evolutivo.entity.user.UserPreferences.Theme;
import es.metrica.sept25.evolutivo.enums.EmissionType;
import es.metrica.sept25.evolutivo.enums.FuelType;
import es.metrica.sept25.evolutivo.enums.MapViewType;
import es.metrica.sept25.evolutivo.repository.GasolineraRepository;
import es.metrica.sept25.evolutivo.repository.UserRepository;
import es.metrica.sept25.evolutivo.service.gasolineras.GasolineraService;
import es.metrica.sept25.evolutivo.service.user.UserServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GasolineraRepository gasolineraRepository;

    @Mock
    private GasolineraService gasolineraService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void save_userWithPassword_encodesAndSaves() {
        User user = new User();
        user.setEmail("a@a.com");
        user.setPassword("1234abcd");

        when(passwordEncoder.encode("1234abcd")).thenReturn("encoded");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        User saved = userService.save(user);

        assertEquals("encoded", saved.getPassword());
        verify(passwordEncoder).encode("1234abcd");
        verify(userRepository).save(user);
    }

    @Test
    void getByEmail_userExists_returnsUser() {
        User user = new User();
        user.setEmail("test@mail.com");

        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));

        Optional<UserResponseDTO> result = userService.getByEmail("test@mail.com");

        assertTrue(result.isPresent());
        assertEquals("test@mail.com", result.get().getEmail());
    }

    @Test
    void getByEmail_userNotExists_returnsEmpty() {
        when(userRepository.findByEmail("x@mail.com")).thenReturn(Optional.empty());

        Optional<UserResponseDTO> result = userService.getByEmail("x@mail.com");

        assertTrue(result.isEmpty());
    }

    @Test
    void getEntityByEmail_returnsUser() {
        User user = new User();
        when(userRepository.findByEmail("a@a.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.getEntityByEmail("a@a.com");

        assertTrue(result.isPresent());
    }

    @Test
    void getAll_returnsAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(new User(), new User()));

        List<UserResponseDTO> result = userService.getAll();

        assertEquals(2, result.size());
    }

    @Test
    void createUser_validUser_returnsUser() {
        UserDTO dto = new UserDTO();
        dto.setEmail("new@mail.com");
        dto.setName("John");
        dto.setSurname("Doe");
        dto.setPassword("1234abcd");
        dto.setPasswordConfirmation("1234abcd");

        when(userRepository.findByEmail("new@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("1234abcd")).thenReturn("encoded");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Optional<User> result = userService.createUser(dto);

        assertTrue(result.isPresent());
        assertEquals("encoded", result.get().getPassword());
    }

    @Test
    void createUser_existingUser_returnsEmpty() {
        UserDTO dto = new UserDTO();
        dto.setEmail("exists@mail.com");
        dto.setPassword("Password123");
        dto.setPasswordConfirmation("Password123");

        when(userRepository.findByEmail("exists@mail.com")).thenReturn(Optional.of(new User()));

        assertTrue(userService.createUser(dto).isEmpty());
    }

    @Test
    void createUser_invalidEmail_returnsEmpty() {
        UserDTO dto = new UserDTO();
        dto.setEmail("bad-email");
        dto.setPassword("1234abcd");
        dto.setPasswordConfirmation("1234abcd");

        assertTrue(userService.createUser(dto).isEmpty());
    }

    @Test
    void createUser_invalidPassword_returnsEmpty() {
        UserDTO dto = new UserDTO();
        dto.setEmail("ok@mail.com");
        dto.setPassword("short");
        dto.setPasswordConfirmation("short");

        assertTrue(userService.createUser(dto).isEmpty());
    }

    @Test
    void createUser_passwordsDoNotMatch_returnsEmpty() {
        UserDTO dto = new UserDTO();
        dto.setEmail("ok@mail.com");
        dto.setPassword("1234abcd");
        dto.setPasswordConfirmation("abcd1234");

        assertTrue(userService.createUser(dto).isEmpty());
    }


    @Test
    void updateRoutePreferences_updatesUser() {
        User user = new User();
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        userService.updateRoutePreferences(
                user,
                List.of("REPSOL"),
                10,
                FuelType.DIESEL,
                1.5,
                MapViewType.SATELLITE,
                false,
                EmissionType.C
        );

        assertEquals(10, user.getRoutePreferences().getRadioKm());
        assertEquals(FuelType.DIESEL, user.getRoutePreferences().getFuelType());
    }

    @Test
    void updateUserPreferences_updatesThemeAndLanguage() {
        User user = new User();
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        userService.updateUserPreferences(user, Theme.DARK, Language.EN);

        assertEquals(Theme.DARK, user.getUserPreferences().getTheme());
        assertEquals(Language.EN, user.getUserPreferences().getLanguage());
    }

    @Test
    void getDefaultPreferences_returnsDefaults() {
        Optional<RoutePreferences> prefs = userService.getDefaultPreferences();

        assertTrue(prefs.isPresent());
        assertEquals(FuelType.ALL, prefs.get().getFuelType());
    }


    @Test
    void removeGasStation_existingUser_removesStation() {
        User user = new User();
        UserSavedGasStation sg = new UserSavedGasStation();
        sg.setAlias("Home");
        user.setSavedGasStations(new ArrayList<>(List.of(sg)));

        when(userRepository.findByEmail("u@mail.com")).thenReturn(Optional.of(user));

        userService.removeGasStation("u@mail.com", "Home");

        assertTrue(user.getSavedGasStations().isEmpty());
    }

    @Test
    void getSavedGasStations_returnsDtos() {
        User user = new User();
        Gasolinera gas = new Gasolinera();
        gas.setIdEstacion(1L);
        gas.setNombreEstacion("Station");
        gas.setMarca("REPSOL");

        UserSavedGasStation sg = new UserSavedGasStation();
        sg.setAlias("Home");
        sg.setGasolinera(gas);
        user.setSavedGasStations(List.of(sg));

        when(userRepository.findByEmail("u@mail.com")).thenReturn(Optional.of(user));

        List<UserSavedGasStationDto> result = userService.getSavedGasStations("u@mail.com");

        assertEquals(1, result.size());
    }

    @Test
    void saveGasStation_externalGasolinera_foundAndSaved() {
        User user = new User();
        user.setSavedGasStations(new ArrayList<>());
        Gasolinera gas = new Gasolinera();
        gas.setIdEstacion(1L);

        when(userRepository.findByEmail("u@mail.com")).thenReturn(Optional.of(user));
        when(gasolineraRepository.findByIdEstacion(1L)).thenReturn(Optional.empty());
        when(gasolineraService.getGasolineraForId(1L)).thenReturn(Optional.of(gas));

        Optional<String> result = userService.saveGasStation("u@mail.com", "Home", 1L);

        assertTrue(result.isEmpty());
        verify(gasolineraRepository).save(gas);
    }
    
    @Test
    void getSimpleInfo_userExists_returnsBasicInfo() {
        String email = "test@mail.com";

        User user = new User();
        user.setEmail(email);
        user.setName("Prueba");
        user.setSurname("Test");

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        Optional<UserBasicInfoDTO> result = userService.getSimpleInfo(email);

        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
        assertEquals("Prueba", result.get().getName());
        assertEquals("Test", result.get().getSurname());
    }

    @Test
    void getSimpleInfo_userNotFound_returnsEmpty() {
        String email = "notfound@mail.com";

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        Optional<UserBasicInfoDTO> result = userService.getSimpleInfo(email);

        assertTrue(result.isEmpty());
    }
    
    @Test
    void renameGasStation_newAliasAlreadyExists_returnsFalse() {
        String email = "test@mail.com";
        
        Gasolinera gas1 = new Gasolinera();
        gas1.setIdEstacion(1L);

        Gasolinera gas2 = new Gasolinera();
        gas2.setIdEstacion(2L);

        UserSavedGasStation gs1 = new UserSavedGasStation();
        gs1.setAlias("Casa");
        gs1.setGasolinera(gas1);

        UserSavedGasStation gs2 = new UserSavedGasStation();
        gs2.setAlias("Trabajo");
        gs2.setGasolinera(gas2);

        User user = new User();
        user.setEmail(email);
        user.setSavedGasStations(new LinkedList<>(List.of(gs1, gs2)));

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        boolean result = userService.renameGasStation(email, "Casa", "Trabajo");

        assertFalse(result);

        verify(userRepository, never()).save(any());
    }
    
    @Test
    void renameGasStation_userNotFound_returnsFalse() {
        String email = "test@mail.com";
        
        Gasolinera gas1 = new Gasolinera();
        gas1.setIdEstacion(1L);

        UserSavedGasStation usg = new UserSavedGasStation();
        usg.setAlias("Casa");
        usg.setGasolinera(gas1);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        boolean result = userService.renameGasStation(email, "Casa", "NuevaCasa");

        assertFalse(result);
    }
    
    @Test
    void renameGasStation_success_returnsTrue() {
        String email = "test@mail.com";

        Gasolinera gas = new Gasolinera();
        gas.setIdEstacion(1L);

        UserSavedGasStation savedGS = new UserSavedGasStation();
        savedGS.setAlias("Casa");
        savedGS.setGasolinera(gas);

        User user = new User();
        user.setEmail(email);
        user.setSavedGasStations(new LinkedList<>(List.of(savedGS)));

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        boolean result = userService.renameGasStation(email, "Casa", "NuevaCasa");

        assertTrue(result);

        assertEquals(1, user.getSavedGasStations().size());
        assertEquals("NuevaCasa", user.getSavedGasStations().get(0).getAlias());

        verify(userRepository).save(user);
    }



}
