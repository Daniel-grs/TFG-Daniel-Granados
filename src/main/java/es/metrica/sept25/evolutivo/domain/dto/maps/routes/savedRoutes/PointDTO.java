package es.metrica.sept25.evolutivo.domain.dto.maps.routes.savedRoutes;

public class PointDTO {

	private String type;
	private String address;

	public PointDTO(String type, String address) {
		this.type= type;
		this.address= address;
	}

	public PointDTO() {
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
