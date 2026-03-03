package es.metrica.sept25.evolutivo.domain.dto.gasolineras;

public class SavedGasStationDTO {

	private String alias;
    private Long idEstacion;
    private String nombreEstacion;
    private String marca;
    private Double latitud;
    private Double longitud;
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public Long getIdEstacion() {
		return idEstacion;
	}
	public void setIdEstacion(Long idEstacion) {
		this.idEstacion = idEstacion;
	}
	public String getNombreEstacion() {
		return nombreEstacion;
	}
	public void setNombreEstacion(String nombreEstacion) {
		this.nombreEstacion = nombreEstacion;
	}
	public String getMarca() {
		return marca;
	}
	public void setMarca(String marca) {
		this.marca = marca;
	}
	public Double getLatitud() {
		return latitud;
	}
	public void setLatitud(Double latitud) {
		this.latitud = latitud;
	}
	public Double getLongitud() {
		return longitud;
	}
	public void setLongitud(Double longitud) {
		this.longitud = longitud;
	}
    
    
}
