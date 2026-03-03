package es.metrica.sept25.evolutivo.domain.dto.user;

import java.util.List;

import es.metrica.sept25.evolutivo.entity.gasolinera.UserSavedGasStation;
import es.metrica.sept25.evolutivo.entity.maps.routes.SavedRoute;

public class UserResponseDTO {

    private String email;
    private String name;
    private String surname;
    List<SavedRoute> savedRoutes;
    List<UserSavedGasStation> savedGasStations;
    
    public UserResponseDTO() {}

    public UserResponseDTO(String email, String name, String surname, List<SavedRoute> savedRoutes, List<UserSavedGasStation> savedGasStations) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.savedRoutes= savedRoutes;
        this.savedGasStations= savedGasStations;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

	public List<SavedRoute> getSavedRoutes() {
		return savedRoutes;
	}

	public void setSavedRoutes(List<SavedRoute> savedRoutes) {
		this.savedRoutes = savedRoutes;
	}

	public List<UserSavedGasStation> getSavedGasStations() {
		return savedGasStations;
	}

	public void setSavedGasStations(List<UserSavedGasStation> savedGasStations) {
		this.savedGasStations = savedGasStations;
	}
    
}
