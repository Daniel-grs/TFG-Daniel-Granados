package es.metrica.sept25.evolutivo.domain.dto.weather;

public class EstadoCielo {
	private String value;
	private Integer periodo;
	private String descripcion;
	
	public EstadoCielo() {
		
	}

	public EstadoCielo(Integer periodo, String descripcion) {
		super();
		this.periodo = periodo;
		this.descripcion = descripcion;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

}