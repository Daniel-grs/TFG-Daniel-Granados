package es.metrica.sept25.evolutivo.domain.dto.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Weather {

	@JsonProperty("nombre")
	private String nombre;
	@JsonProperty("provincia")
	private String provincia;
	@JsonProperty("prediccion")
	private Prediccion prediccion;

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public Prediccion getPrediccion() {
		return prediccion;
	}

	public void setPrediccion(Prediccion prediccion) {
		this.prediccion = prediccion;
	}

	@Override
	public String toString() {
		return "Weather [nombre=" + nombre + ", provincia=" + provincia + ", prediccion=" + prediccion + "]";
	}

}

