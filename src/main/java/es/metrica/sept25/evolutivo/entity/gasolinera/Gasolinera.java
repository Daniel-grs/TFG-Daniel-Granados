package es.metrica.sept25.evolutivo.entity.gasolinera;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

@Entity
public class Gasolinera {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long idEstacion;

	private String nombreEstacion;

	private String marca;

	private String horario;

	private Double longitud;
	private Double latitud;

	private String direccion;

	private String localidad;

	private Long idMunicipio;

	private String codPostal;

	private String provincia;

	private String provinciaDistrito;

	// Averiguar que significa
	private String tipoVenta;

	@Transient
	private String lastUpdate;

	@Transient
	@JsonProperty("Gasolina95")
	private Double precioGasolina95;

	@Transient
	@JsonProperty("Gasolina95_media")
	private Double precioGasolina95Media;

	@Transient
	@JsonProperty("Gasolina98")
	private Double precioGasolina98;

	@Transient
	@JsonProperty("Gasolina98_media")
	private Double precioGasolina98Media;

	@Transient
	@JsonProperty("Diesel")
	private Double precioDiesel;

	@Transient
	@JsonProperty("Diesel_media")
	private Double precioDieselMedia;

	@Transient
	@JsonProperty("DieselPremium")
	private Double precioDieselPremium;

	@Transient
	@JsonProperty("DieselPremium_media")
	private Double precioDieselPremiumMedia;

	@Transient
	@JsonProperty("DieselB")
	private Double precioDieselB;

	@Transient
	@JsonProperty("DieselB_media")
	private Double precioDieselBMedia;

	@Transient
	@JsonProperty("GLP")
	private Double precioGlp;

	@Transient
	@JsonProperty("GLP_media")
	private Double precioGlpMedia;
	
	public Gasolinera() {
		
	}

	public Gasolinera(Long idEstacion, String nombreEstacion, String marca, String horario, String lastUpdate,
			Double longitud, Double latitud, String direccion, String localidad, Long idMunicipio, String codPostal,
			String provincia, String provinciaDistrito, String tipoVenta, Double precioGasolina95,
			Double precioGasolina95Media, Double precioGasolina98, Double precioGasolina98Media, Double precioDiesel,
			Double precioDieselMedia, Double precioDieselB, Double precioDieselBMedia, Double precioDieselPremium,
			Double precioDieselPremiumMedia, Double precioGlp, Double precioGlpMedia) {
		super();
		this.idEstacion = idEstacion;
		this.nombreEstacion = nombreEstacion;
		this.marca = marca;
		this.horario = horario;
		this.lastUpdate = lastUpdate;
		this.longitud = longitud;
		this.latitud = latitud;
		this.direccion = direccion;
		this.localidad = localidad;
		this.idMunicipio = idMunicipio;
		this.codPostal = codPostal;
		this.provincia = provincia;
		this.provinciaDistrito = provinciaDistrito;
		this.tipoVenta = tipoVenta;
		this.precioGasolina95 = precioGasolina95;
		this.precioGasolina95Media = precioGasolina95Media;
		this.precioGasolina98 = precioGasolina98;
		this.precioGasolina98Media = precioGasolina98Media;
		this.precioDiesel = precioDiesel;
		this.precioDieselMedia = precioDieselMedia;
		this.precioDieselB = precioDieselB;
		this.precioDieselBMedia = precioDieselBMedia;
		this.precioDieselPremium = precioDieselPremium;
		this.precioDieselPremiumMedia = precioDieselPremiumMedia;
		this.precioGlp = precioGlp;
		this.precioGlpMedia = precioGlpMedia;
	}

	public Long getIdEstacion() {
		return this.idEstacion;
	}

	public void setIdEstacion(Long idEstacion) {
		this.idEstacion = idEstacion;
	}

	public String getNombreEstacion() {
		return this.nombreEstacion;
	}

	public void setNombreEstacion(String nombreEstacion) {
		this.nombreEstacion = nombreEstacion;
	}

	public String getMarca() {
		return this.marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public String getHorario() {
		return this.horario;
	}

	public void setHorario(String horario) {
		this.horario = horario;
	}

	public String getLastUpdate() {
		return this.lastUpdate;
	}

	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public Double getLongitud() {
		return this.longitud;
	}

	public void setLongitud(Double longitud) {
		this.longitud = longitud;
	}

	public Double getLatitud() {
		return this.latitud;
	}

	public void setLatitud(Double latitud) {
		this.latitud = latitud;
	}

	public String getDireccion() {
		return this.direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getLocalidad() {
		return this.localidad;
	}

	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}

	public Long getIdMunicipio() {
		return idMunicipio;
	}

	public void setIdMunicipio(Long idMunicipio) {
		this.idMunicipio = idMunicipio;
	}

	public String getCodPostal() {
		return this.codPostal;
	}

	public void setCodPostal(String codPostal) {
		this.codPostal = codPostal;
	}

	public String getProvincia() {
		return this.provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public String getProvinciaDistrito() {
		return this.provinciaDistrito;
	}

	public void setProvinciaDistrito(String provinciaDistrito) {
		this.provinciaDistrito = provinciaDistrito;
	}

	public String getTipoVenta() {
		return this.tipoVenta;
	}

	public void setTipoVenta(String tipoVenta) {
		this.tipoVenta = tipoVenta;
	}

	public Double getPrecioGasolina95() {
		return this.precioGasolina95;
	}

	public void setPrecioGasolina95(Double precioGasolina95) {
		this.precioGasolina95 = precioGasolina95;
	}

	public Double getPrecioGasolina95Media() {
		return this.precioGasolina95Media;
	}

	public void setPrecioGasolina95Media(Double precioGasolina95Media) {
		this.precioGasolina95Media = precioGasolina95Media;
	}

	public Double getPrecioGasolina98() {
		return this.precioGasolina98;
	}

	public void setPrecioGasolina98(Double precioGasolina98) {
		this.precioGasolina98 = precioGasolina98;
	}

	public Double getPrecioGasolina98Media() {
		return this.precioGasolina98Media;
	}

	public void setPrecioGasolina98Media(Double precioGasolina98Media) {
		this.precioGasolina98Media = precioGasolina98Media;
	}

	public Double getPrecioDiesel() {
		return this.precioDiesel;
	}

	public void setPrecioDiesel(Double precioDiesel) {
		this.precioDiesel = precioDiesel;
	}

	public Double getPrecioDieselMedia() {
		return this.precioDieselMedia;
	}

	public void setPrecioDieselMedia(Double precioDieselMedia) {
		this.precioDieselMedia = precioDieselMedia;
	}

	public Double getPrecioDieselB() {
		return this.precioDieselB;
	}

	public void setPrecioDieselB(Double precioDieselB) {
		this.precioDieselB = precioDieselB;
	}

	public Double getPrecioDieselBMedia() {
		return this.precioDieselBMedia;
	}

	public void setPrecioDieselBMedia(Double precioDieselBMedia) {
		this.precioDieselBMedia = precioDieselBMedia;
	}

	public Double getPrecioDieselPremium() {
		return this.precioDieselPremium;
	}

	public void setPrecioDieselPremium(Double precioDieselPremium) {
		this.precioDieselPremium = precioDieselPremium;
	}

	public Double getPrecioDieselPremiumMedia() {
		return this.precioDieselPremiumMedia;
	}

	public void setPrecioDieselPremiumMedia(Double precioDieselPremiumMedia) {
		this.precioDieselPremiumMedia = precioDieselPremiumMedia;
	}

	public Double getPrecioGlp() {
		return this.precioGlp;
	}

	public void setPrecioGlp(Double precioGlp) {
		this.precioGlp = precioGlp;
	}

	public Double getPrecioGlpMedia() {
		return this.precioGlpMedia;
	}

	public void setPrecioGlpMedia(Double precioGlpMedia) {
		this.precioGlpMedia = precioGlpMedia;
	}

	@Override
	public String toString() {
		return "Gasolinera [idEstacion=" + idEstacion + ", nombreEstacion=" + nombreEstacion + ", marca=" + marca
				+ ", horario=" + horario + ", lastUpdate=" + lastUpdate + ", longitud=" + longitud + ", latitud="
				+ latitud + ", direccion=" + direccion + ", localidad=" + localidad + ", idMunicipio=" + idMunicipio
				+ ", codPostal=" + codPostal + ", provincia=" + provincia + ", provinciaDistrito=" + provinciaDistrito
				+ ", tipoVenta=" + tipoVenta + ", precioGasolina95=" + precioGasolina95 + ", precioGasolina95Media="
				+ precioGasolina95Media + ", precioGasolina98=" + precioGasolina98 + ", precioGasolina98Media="
				+ precioGasolina98Media + ", precioDiesel=" + precioDiesel + ", precioDieselMedia=" + precioDieselMedia
				+ ", precioDieselPremium=" + precioDieselPremium + ", precioDieselPremiumMedia="
				+ precioDieselPremiumMedia + ", precioDieselB=" + precioDieselB + ", precioDieselBMedia="
				+ precioDieselBMedia + ", precioGlp=" + precioGlp + ", precioGlpMedia=" + precioGlpMedia + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(codPostal, direccion, horario, id, idEstacion, idMunicipio, lastUpdate, latitud, localidad,
				longitud, marca, nombreEstacion, precioDiesel, precioDieselB, precioDieselBMedia, precioDieselMedia,
				precioDieselPremium, precioDieselPremiumMedia, precioGasolina95, precioGasolina95Media,
				precioGasolina98, precioGasolina98Media, precioGlp, precioGlpMedia, provincia, provinciaDistrito,
				tipoVenta);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Gasolinera other = (Gasolinera) obj;
		return Objects.equals(codPostal, other.codPostal) 
				&& Objects.equals(direccion, other.direccion)
				&& Objects.equals(horario, other.horario) 
				&& Objects.equals(id, other.id)
				&& Objects.equals(idEstacion, other.idEstacion) 
				&& Objects.equals(idMunicipio, other.idMunicipio)
				&& Objects.equals(lastUpdate, other.lastUpdate) 
				&& Objects.equals(latitud, other.latitud)
				&& Objects.equals(longitud, other.longitud)
				&& Objects.equals(localidad, other.localidad) 
				&& Objects.equals(marca, other.marca) 
				&& Objects.equals(nombreEstacion, other.nombreEstacion)
				&& Objects.equals(precioDiesel, other.precioDiesel)
				&& Objects.equals(precioDieselMedia, other.precioDieselMedia)
				&& Objects.equals(precioDieselB, other.precioDieselB)
				&& Objects.equals(precioDieselBMedia, other.precioDieselBMedia)
				&& Objects.equals(precioDieselPremium, other.precioDieselPremium)
				&& Objects.equals(precioDieselPremiumMedia, other.precioDieselPremiumMedia)
				&& Objects.equals(precioGasolina95, other.precioGasolina95)
				&& Objects.equals(precioGasolina95Media, other.precioGasolina95Media)
				&& Objects.equals(precioGasolina98, other.precioGasolina98)
				&& Objects.equals(precioGasolina98Media, other.precioGasolina98Media)
				&& Objects.equals(precioGlp, other.precioGlp) 
				&& Objects.equals(precioGlpMedia, other.precioGlpMedia)
				&& Objects.equals(provincia, other.provincia)
				&& Objects.equals(provinciaDistrito, other.provinciaDistrito)
				&& Objects.equals(tipoVenta, other.tipoVenta);
	}
}
