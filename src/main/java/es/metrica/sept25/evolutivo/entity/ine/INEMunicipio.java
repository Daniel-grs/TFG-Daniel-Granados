package es.metrica.sept25.evolutivo.entity.ine;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class INEMunicipio {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonProperty("Nombre")
	private String nombre;

	@JsonProperty("Codigo")
	private String codigoINE;

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCodigoINE() {
		return codigoINE;
	}

	public void setCodigoINE(String codigoINE) {
		this.codigoINE = codigoINE;
	}
}
