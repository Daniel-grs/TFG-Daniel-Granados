package es.metrica.sept25.evolutivo.entity.gasolinera;

import com.fasterxml.jackson.annotation.JsonBackReference;

import es.metrica.sept25.evolutivo.entity.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_saved_gas_stations")
public class UserSavedGasStation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String alias;

	@ManyToOne
	@JsonBackReference("user-gasStations")
	private User user;

	@ManyToOne(cascade = CascadeType.PERSIST)
	private Gasolinera gasolinera;

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Gasolinera getGasolinera() {
		return gasolinera;
	}

	public void setGasolinera(Gasolinera gasolinera) {
		this.gasolinera = gasolinera;
	}

}
