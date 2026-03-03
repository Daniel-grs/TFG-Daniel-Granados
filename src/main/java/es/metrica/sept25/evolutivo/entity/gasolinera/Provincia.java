package es.metrica.sept25.evolutivo.entity.gasolinera;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Provincia {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(name = "req_id")
	Long idProvincia;

	@Column(name = "nombre_provincia")
	String nombreProvincia;

	public Provincia() {
	};

	public Provincia(Long idProvincia, String nombreProvincia) {
		super();
		this.idProvincia = idProvincia;
		this.nombreProvincia = nombreProvincia;
	}

	public Long getIdProvincia() {
		return idProvincia;
	}

	public void setIdProvincia(Long idProvincia) {
		this.idProvincia = idProvincia;
	}

	public String getNombreProvincia() {
		return nombreProvincia;
	}

	public void setNombreProvincia(String nombreProvincia) {
		this.nombreProvincia = nombreProvincia;
	}

	@Override
	public String toString() {
		return "Provincia[id=" + this.idProvincia + ", nombre=" + this.nombreProvincia + "]";
	}
}
