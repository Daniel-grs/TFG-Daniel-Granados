package es.metrica.sept25.evolutivo.entity.gasolinera;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Municipio {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(name = "req_id")
	Long idMunicipio;

	@Column(name = "nombre_municipio")
	String nombreMunicipio;

	@Column(name = "prov_id")
	Long idProvincia;

	public Municipio() {
	};

	public Municipio(Long idMunicipio, String nombreMunicipio, Long idProvincia) {
		super();
		this.idMunicipio = idMunicipio;
		this.nombreMunicipio = nombreMunicipio;
		this.idProvincia = idProvincia;
	}

	public Long getIdMunicipio() {
		return idMunicipio;
	}

	public void setIdMunicipio(Long idMunicipio) {
		this.idMunicipio = idMunicipio;
	}

	public String getNombreMunicipio() {
		return nombreMunicipio;
	}

	public void setNombreMunicipio(String nombreMunicipio) {
		this.nombreMunicipio = nombreMunicipio;
	}

	public Long getIdProvincia() {
		return this.idProvincia;
	}

	public void setIdProvincia(Long idProvincia) {
		this.idProvincia = idProvincia;
	}

	@Override
	public String toString() {
		return "Municipio [idMunicipio=" + idMunicipio + ", nombreMunicipio=" + nombreMunicipio + ", idProvincia="
				+ idProvincia + "]";
	}
}
