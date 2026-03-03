package es.metrica.sept25.evolutivo.domain.dto.weather;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Prediccion {
	@JsonProperty("dia")
	private List<Dia> dia;

	public List<Dia> getDia() {
		return dia;
	}

	public void setDia(List<Dia> dia) {
		this.dia = dia;
	}

	@Override
	public String toString() {
		return "Prediccion [dia=" + dia + "]";
	}


}
