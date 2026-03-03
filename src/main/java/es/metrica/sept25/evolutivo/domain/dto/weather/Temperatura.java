package es.metrica.sept25.evolutivo.domain.dto.weather;

public class Temperatura {
	private Double value;
	private Integer periodo;

	public Temperatura() {
		
	}
	
	public Temperatura(int periodo, double value) {
		super();
		this.periodo=periodo;
		this.value=value;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}


}