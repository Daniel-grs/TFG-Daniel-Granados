package es.metrica.sept25.evolutivo.domain.dto.weather;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Dia {

	private List<EstadoCielo> estadoCielo;
	private List<Temperatura> temperatura;
	private String fecha;

	public List<EstadoCielo> getEstadoCielo() {
		return estadoCielo;
	}

	public void setEstadoCielo(List<EstadoCielo> estadoCielo) {
		this.estadoCielo = estadoCielo;
	}

	public List<Temperatura> getTemperatura() {
		return temperatura;
	}

	public void setTemperatura(List<Temperatura> temperatura) {
		this.temperatura = temperatura;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	@Override
	public String toString() {
		return "Dia [estadoCielo=" + estadoCielo + ", temperatura=" + temperatura + ", fecha=" + fecha + "]";
	}
	


}
